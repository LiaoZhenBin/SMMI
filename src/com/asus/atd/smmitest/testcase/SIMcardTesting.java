package com.asus.atd.smmitest.testcase;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.asus.atd.smmitest.R;
import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.mediatek.telephony.TelephonyManagerEx;

/**
 * Created by luyuan on 2016/1/14.
 */
public class SIMcardTesting extends BaseActivity {

    int sim1State;
    int sim2State;

    TextView sim1;
    TextView tvIMSI1;
    TextView sim2;
    TextView tvIMSI2;

    TelephonyManagerEx tm;

    //String sim1StateStr;
    //String sim2StateStr;


    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    private static final String mModuleName = "SIMcard Test";
    private ModuleManager mModuleManager;

    public static int mSucCount = 0;
    public static int mFailCount = 0;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.getActionBar().hide();
        TextView title = (TextView) findViewById(R.id.title_simcard);
        title.setText(getString(R.string.title_test_sim));


        tm = TelephonyManagerEx.getDefault();
        //获得simcard的状态
        sim1State = tm.getSimState(0);
        sim2State = tm.getSimState(1);
        sim1 = (TextView) findViewById(R.id.sim1_state);
        sim2 = (TextView) findViewById(R.id.sim2_state);

        setTitle(getString(R.string.title_test_sim));

        tvIMSI1 = (TextView) findViewById(R.id.imsi_1);
        tvIMSI1.setText(getString(R.string.imsi_1) + tm.getSubscriberId(0));
        tvIMSI2 = (TextView) findViewById(R.id.imsi_2);
        tvIMSI2.setText(getString(R.string.imsi_2) + tm.getSubscriberId(1));
        register();
    }

    //注册广播
    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SIM_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

    }

    //广播接收器接收系统的广播，监听simcard的状态
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                //liaozhenbin@wind-mobi.com 20160719 mod begin
            if ((sim1State == TelephonyManager.SIM_STATE_READY) && (sim2State == TelephonyManager.SIM_STATE_READY)) {
                //liaozhenbin@wind-mobi.com 20160719 mod end
                mSucCount++;
                setTestResult(Constants.TEST_PASS, mSucCount);
                handler.sendEmptyMessageDelayed(0, 1500);
                handler.sendEmptyMessageDelayed(1, 2500);
                handler.sendEmptyMessageDelayed(2, 4000);

            } else {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                handler.sendEmptyMessageDelayed(0, 1500);
                handler.sendEmptyMessageDelayed(1, 2500);
                handler.sendEmptyMessageDelayed(3, 4000);

            }
        } //onreceive end
    };

    public void clearStatus() {
        sim1.setText(getString(R.string.sim1_state));
        tvIMSI1.setText(getString(R.string.imsi_1));
        sim2.setText(getString(R.string.sim2_state));
        tvIMSI2.setText(getString(R.string.imsi_2));
    }

    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, mModuleName);
            finish();
            //  Log.d(TAG, "SIMcardTest finished ,send broadcast to run the next test module");
        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModuleName);
            setResult(resultCode);
            finish();
        }
    }


    public void setTestResult(int code, int count) {
        for (TestCase module : mModuleManager.mTestModule) {
            if (module.getEnName().equals("SIMcard Test")) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    //liaozhenbin@wind-mobi.com 20160702 mod begin
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //liaozhenbin@wind-mobi.com 20160719 mod begin
                    sim1.setText(getString(R.string.sim1_state) + (sim1State == 5 ? "YES" : "NO"));
                    //liaozhenbin@wind-mobi.com 20160719 mod end
                    tvIMSI1.setText(getString(R.string.imsi_1) + tm.getSubscriberId(0));
                    break;
                case 1:
                    //liaozhenbin@wind-mobi.com 20160719 mod begin
                    sim2.setText(getString(R.string.sim2_state) + (sim2State == 5 ? "YES" : "NO"));
                    //liaozhenbin@wind-mobi.com 20160719 mod end
                    tvIMSI2.setText(getString(R.string.imsi_2) + tm.getSubscriberId(1));
                    break;
                case 2:
                    finishActivity(Constants.TEST_PASS);
                    break;
                case 3:
                    finishActivity(Constants.TEST_FAILED);
                    break;
            }

        }
    };
    //liaozhenbin@wind-mobi.com 20160702 mod end


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_simcard);
    }

    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }


    protected void onDestroy() {
        super.onDestroy();
        clearStatus();
        //注销广播接收器
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);

    }

    //liaozhenbin@wind-mobi.com 20160702 mod begin

    @Override
    public void onBackPressed() {

    }
    //liaozhenbin@wind-mobi.com 20160702 mod end
}
