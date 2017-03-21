package com.asus.atd.smmitest.testcase;

import android.telephony.TelephonyManager;
import android.view.View;
import android.view.KeyEvent;
import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by heliguang on 2016/1/21.
 */
public class ModemTesting extends BaseActivity {
    private TelephonyManager mTelMgr;
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    private static final String mModuleName = "Modem Test";
    private ModuleManager mModuleManager ;
    private boolean hasDeviceId;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_modembus);
        this.getActionBar().hide();
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

    protected void onResume() {
        super.onResume();
        mTelMgr= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        startTest();
    }

    public void startTest() {
        hasDeviceId = (mTelMgr.getDeviceId() != null) ? true : false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (hasDeviceId) {
                    mSucCount++;
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                } else {
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                }
            }
        }, 1500);
    }

    public void setTestResult(int code,int count){

        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("Modem Test")){
                if(code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    public void finishActivity(int resultCode){
        if (MainManager.getInstance(this).mIsAutoTest){
            MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
            finish();
            Log.d(TAG, "ModemTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
            setResult(resultCode);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
