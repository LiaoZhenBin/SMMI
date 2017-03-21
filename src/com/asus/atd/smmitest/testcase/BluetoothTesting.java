package com.asus.atd.smmitest.testcase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
 * Created by lizusheng on 2015/12/29.
 */
public class BluetoothTesting extends BaseActivity{
    private TextView tv_status;
    private TextView tv_address;
    private TextView tv_device;
    private static final int MSG_BT_ON = 100;
    private static final int MSG_BT_OFF = 101;
    private static final int MSG_BT_TEST_PASS = 102;
    private static final int MSG_BT_TEST_FAIL = 103;
    private boolean mOrigBTState = false;
    private BluetoothAdapter mBtAdapter = null;
    private StringBuilder mDeviceList = new StringBuilder();
    private static final String mModuleName = "Bluetooth Test";
    private ModuleManager mModuleManager ;
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    //huangzhijian@wind-mobi.com add 20161103 start
    public boolean mregisterOnce=true;
    //huangzhijian@wind-mobi.com add 20161103 end

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_bt);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        tv_status= (TextView) findViewById(R.id.tv_status_bt);
        tv_address= (TextView) findViewById(R.id.tv_address_bt);
        tv_device= (TextView) findViewById(R.id.tv_device_bt);
     }


    public void register(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //huangzhijian@wind-mobi.com add 20161103 start
        mregisterOnce=true;
        //huangzhijian@wind-mobi.com add 20161103 end
        registerReceiver(mReceiver, intentFilter);
    }

    public void enableBluetooth(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            finish();
            Log.d(TAG, "BtAdapter is null");
        } else {
            if (!mBtAdapter.isEnabled()) {
                mBtAdapter.enable();
                Log.d(TAG, "enable BtAdapter");
            } else {
                mOrigBTState = true;
            }
            if (mBtAdapter.isEnabled()) {
                mHandler.sendEmptyMessage(MSG_BT_ON);
            }
        }
    }

    public void disEnableBluetooth(){
        Log.d(TAG, "disEnableBluetooth");
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
            if (mBtAdapter.isEnabled()) {
                mBtAdapter.disable();
                Log.d(TAG, "disEnable BtAdapter");
            }

            if (!mOrigBTState) {
                Intent newIntent = new Intent(Constants.ACTION_EMODE_RESTORE_BT);
                newIntent.setPackage(getPackageName());
                startService(newIntent);
            }
        }
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what) {
                case MSG_BT_ON:
                    Log.d(TAG, "start to disCovery");
                    mBtAdapter.startDiscovery();
                    tv_status.setText(getString(R.string.status_test_bt) + "  " +getString(R.string.status_on_bt));
                    tv_address.setText(getString(R.string.address_test_bt)+ "  "
                            +mBtAdapter.getAddress());
                    break;
                case MSG_BT_TEST_PASS:
//                    zhangtao@wind-mobi.com 2016/1/14 modified begain

                    // zhangtao@wind-mobi.com  2016/1/12  modified begain

                       /* mFailCount++;
                        setTestResult(Constants.TEST_FAILED,mFailCount);
                        disEnableBluetooth();
                        finishActivity(Constants.TEST_FAILED);
                          */
                 /*   if(FLAG==false){
                        mFailCount++;
                        setTestResult(Constants.TEST_FAILED,mFailCount);
                        disEnableBluetooth();
                        finishActivity(Constants.TEST_FAILED);}
                        */
                    //zhangtao@wind-mobi.com  2016/1/12 modified end

                    mSucCount++;
                    setTestResult(Constants.TEST_PASS,mSucCount);
                    disEnableBluetooth();
                    finishActivity(Constants.TEST_PASS);
//                    zhangtao@wind-mobi.com 20161/14 modified end
                    Log.d(TAG, "Bluetooth test is pass");
                    break;
                case MSG_BT_TEST_FAIL:
//             zhangtao@wind-mobi.com 2016/1/14 modified begain

             /*   mFailCount++;
                    setTestResult(Constants.TEST_FAILED,mFailCount);
                    disEnableBluetooth();
                    finishActivity(Constants.TEST_FAILED);*/
                    if(FLAG==false){
                        mFailCount++;
                        setTestResult(Constants.TEST_FAILED,mFailCount);
                        disEnableBluetooth();
                        finishActivity(Constants.TEST_FAILED);}
//            zhangtao@wind-mobi.com 2016/1/14 modified end
                    Log.d(TAG, "Bluetooth test is failed");
                    break;
                default:
                    break;
            }
        }

    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    mHandler.sendEmptyMessage(MSG_BT_ON);
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDeviceList.toString().contains(device.getAddress())) {
                    mDeviceList.append(device.getName());
                    mDeviceList.append(":");
                    mDeviceList.append(device.getAddress());
                    mDeviceList.append("\n");
                    //huangzhijian@wind-mobi.com modify 20161103 start
                    //mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_PASS, 500);
                    if(mregisterOnce){
                        mregisterOnce=false;
                        mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_PASS, 500);
                        Log.d(TAG, "sendEmptyMessageDelayed====="+mregisterOnce);
                    }
                    //huangzhijian@wind-mobi.com modify 20161103 end
                }
                tv_device.setText(getString(R.string.devicelist_test_bt)+"\n"+mDeviceList);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //    mScanStartTime = System.currentTimeMillis();
                tv_status.setText(getString(R.string.status_test_bt)+ getString(R.string.status_scanning_bt));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mDeviceList.length() == 0) {
                    //huangzhijian@wind-mobi.com modify 20161103 start
                    //mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_FAIL, 500);
                    if(mregisterOnce){
                        mregisterOnce=false;
                        mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_FAIL, 500);
                        Log.d(TAG, "sendEmptyMessageDelayed====="+mregisterOnce);
                    }
                    //huangzhijian@wind-mobi.com modify 20161103 end
                    tv_status.setText(getString(R.string.no_bt_device));
                }
            }
        }
    };

    /**
     * 清理状态
     */
    public void clearStatus(){
//        zhangtao@wind-mobi.com 2016/1/13 add begain
        FLAG=false;
//        zhangtao@wind-mobi.com 2016/1/13  add end

        tv_status.setText(getString(R.string.status_test_bt)+ getString(R.string.status_off_bt));
        tv_address.setText(getString(R.string.address_test_bt)+ getString(R.string.unknown));
        tv_device.setText(getString(R.string.devicelist_test_bt));
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
           Log.d(TAG,"BluetoothTest finished ,send broadcast to run the next test module");
       }else {
           SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
           setResult(resultCode);
           finish();
       }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);
        this.setTitle(mModuleName);
    }


    public void setTestResult(int code,int count){
        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("Bluetooth Test")){
                if(code == Constants.TEST_PASS)
                module.setSuccessCount(count);
                else
                module.setFailCount(count);
            }
        }
    }

    @Override
    public void startTest() {
        disEnableBluetooth();
        clearStatus();
        enableBluetooth();
        Log.d(TAG,"start to test bluetooth");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        register();
        startTest();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disEnableBluetooth();
        unregisterReceiver(mReceiver);
    }

    /**
     * 手动结束测试返回
     */
    //zhangtao@wind-mobi.com  2016/1/12  add begain
    boolean FLAG=false;
    //zhangtao@wind-mobi.com  2016/1/12 add end
    @Override
    public void onBackPressed() {
        disEnableBluetooth();
        //zhangtao@wind-mobi.com  2016/1/12 add begain
        mFailCount++;
        mHandler.removeMessages(MSG_BT_TEST_FAIL);
        mHandler.removeMessages(MSG_BT_TEST_PASS);
        FLAG=true;
        //zhangtao@wind-mobi.com  2016/1/12 add end

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
