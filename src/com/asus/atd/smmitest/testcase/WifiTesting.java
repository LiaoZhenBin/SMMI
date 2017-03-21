package com.asus.atd.smmitest.testcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

import java.util.List;

/**
 * Created by liaozhenbin on 2016/1/8.
 */
//liaozhenbin@wind-mobi.com 20160111 mod begin
//实现WiFi Test模块
public class WifiTesting extends BaseActivity {
    private TextView tv_status;
    private TextView tv_address;
    private TextView tv_device;
    private ModuleManager mModuleManager;
    private WifiManager mWifiManager = null;
    private StringBuilder mDeviceList = new StringBuilder();
    private static final int WIFI_SCAN_RESULTS_SIZE = 1;
    private static final String mModuleName = "WiFi Test";

    private static final int MSG_WIFI_ENABLED = 101;
    private static final int MSG_WIFI_SCAN_RESULTS_AVAILABLE = 102;
    private static final int MSG_WIFI_TEST_PASS = 103;
    private static final int MSG_WIFI_TEST_FAIL = 104;
    private static int mSucCount = 0;
    private static int mFailCount = 0;
    private static boolean WIFI_INIT_STATE = false;



    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_WIFI_ENABLED:
                    mWifiManager.startScan();
                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                    tv_status.setText(getString(R.string.status_test_wifi)+getString(R.string.status_on_wifi));
                    tv_address.setText(getString(R.string.address_test_wifi)+ wifiInfo.getMacAddress());
                    break;
                case MSG_WIFI_SCAN_RESULTS_AVAILABLE:
                    List<ScanResult> scanList = mWifiManager.getScanResults();
                    for (ScanResult scanResult : scanList) {
                        if (!mDeviceList.toString().contains(scanResult.SSID)) {
                            mDeviceList.append(scanResult.SSID);
                            mDeviceList.append("\n");
                        }
                    }
                    tv_device.setText(getString(R.string.devicelist_test_wifi)+"\n"+mDeviceList.toString());
                    if (scanList.size() >= WIFI_SCAN_RESULTS_SIZE) {
//                        mBtnPass.setVisibility(View.VISIBLE);
                        mHandler.sendEmptyMessageDelayed(MSG_WIFI_TEST_PASS, 500);
                    }else{
                        mHandler.sendEmptyMessageDelayed(MSG_WIFI_TEST_FAIL, 500);  
                    }
                    break;
                case MSG_WIFI_TEST_PASS:
                    mSucCount++;
                    disEnableWifi();
                    Log.d(TAG, "mSucCount:" + mSucCount);
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                    Log.d(TAG, "Wifi test is pass");
                    break;
                case MSG_WIFI_TEST_FAIL:
                    mFailCount++;
                    disEnableWifi();
                    setTestResult(Constants.TEST_FAILED, mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                    Log.d(TAG, "Wifi test is failed");
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_wifi);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        tv_status = (TextView) findViewById(R.id.tv_status_wifi);
        tv_address = (TextView) findViewById(R.id.tv_address_wifi);
        tv_device = (TextView) findViewById(R.id.tv_device_wifi);
    }

    /**
     * 测试正常结束返回
     * 根据是单项测试/自动测试：
     * 自动测试 --- MainManager.getInstance().sendFinishBroadcast()
     * 单项测试 --- 更新测试结果
     * @param resultCode
     */
    public void finishActivity(int resultCode){
        if (MainManager.getInstance(this).mIsAutoTest){
            MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
            finish();
            Log.d(TAG, "WifiTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
            setResult(resultCode);
            finish();
        }
    }
    @Override
    protected void setListener() {

    }


    /**
     * 开始测试
     */
    @Override
    public void startTest() {
        disEnableWifi();
        clearStatus();
        enableWifi();
        Log.d(TAG, "start to test wifi");
    }

    /**
     * 关闭WiFi
     */
    private void disEnableWifi() {
        Log.d(TAG, "disEnableWifi");
        if (mWifiManager != null) {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
                Log.d(TAG, "disEnable WifiManager");
            }

//            if (!mOrigBTState) {
//                Intent newIntent = new Intent(Constants.ACTION_EMODE_RESTORE_BT);
//                newIntent.setPackage(getPackageName());
//                startService(newIntent);
//            }
        }
    }

    /**
     * 开启WiFi
     */
    private void enableWifi() {
        //mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            tv_status.setText(getString(R.string.status_test_wifi) + getString(R.string.status_off_wifi));
            mWifiManager.setWifiEnabled(true);
        } else {
//            mOrigWiFiState = true;
            mHandler.sendEmptyMessage(MSG_WIFI_ENABLED);
        }
    }

    /**
     * 清理状态
     */
    private void clearStatus() {
        tv_status.setText(getString(R.string.status_test_wifi) + getString(R.string.status_off_wifi));
        tv_address.setText(getString(R.string.address_test_wifi) + getString(R.string.unknown));
        tv_device.setText(getString(R.string.devicelist_test_wifi));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //把底下的button移除
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        register();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WIFI_INIT_STATE = mWifiManager.isWifiEnabled();
        startTest();
    }

    /**
     * 定义WiFi广播接受者监听WiFi变化
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (state == WifiManager.WIFI_STATE_ENABLED) {
                    mHandler.sendEmptyMessage(MSG_WIFI_ENABLED);
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                mHandler.sendEmptyMessage(MSG_WIFI_SCAN_RESULTS_AVAILABLE);
            }
        }
    };

    /**
     * 动态注册Wifi广播接受者
     */
    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * Activity销毁,顺便把动态注册的广播接受器注销
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //disEnableWifi();
        if (WIFI_INIT_STATE != mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(WIFI_INIT_STATE);
        }
        unregisterReceiver(mReceiver);
    }

    /**
     * 设置测试返回信息
     * @param code
     * @param count
     */
    public void setTestResult(int code,int count){
        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("WiFi Test")){
                if(code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    @Override
    protected void init() {
        {
            mModuleManager = ModuleManager.getInstance(this);
            this.setTitle(mModuleName);
        }
    }

    /**
     * 手动结束测试返回
     */
    @Override
    public void onBackPressed() {
        mFailCount++;
        //liyong01@wind-mobi.com modify for 2016.06.22
        //mHandler.removeMessages(MSG_WIFI_TEST_PASS);
        mHandler.removeMessages(MSG_WIFI_TEST_FAIL);
        //liyong01@wind-mobi.com modify for 2016.06.22
        mHandler.removeMessages(MSG_WIFI_TEST_PASS);
        disEnableWifi();
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED ,mModuleName);
        setTestResult(Constants.TEST_FAILED,mFailCount);
        setResult(Constants.TEST_FAILED);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
//liaozhenbin@wind-mobi.com 20160111 mod end