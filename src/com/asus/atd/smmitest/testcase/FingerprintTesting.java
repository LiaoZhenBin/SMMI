package com.asus.atd.smmitest.testcase;

import android.app.Service;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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

import java.io.File;

/**
 * Created by lizusheng on 2016/6/15.
 */
public class FingerprintTesting extends BaseActivity {
    private TextView tv_status;
    private static final String mModuleName = "Fingerprint Test";
    private static final String GOODIX_FP = "goodix_fp" ;
    private static final String MARRY_FP = "madev0";
    private static final String MARRY_FP_TEE = "madev";
    private static final String SILEAD_FP = "silead_fp_dev";
    private ModuleManager mModuleManager ;
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    private static final int MSG_FINGERPRINT_TEST_SUCCESS = 0;
    private static final int MSG_FINGERPRINT_TEST_FAILED = 1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_fingerprint);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        tv_status= (TextView) findViewById(R.id.tv_status_fp);
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
            Log.d(TAG,"FingerprintTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModuleName);
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
            if (module.getEnName().equals("Fingerprint Test")){
                if(code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_FINGERPRINT_TEST_SUCCESS:
                    mSucCount++;
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                    break;
                case MSG_FINGERPRINT_TEST_FAILED:
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED,mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                    break;
                default:
                    break;
            }
        }
    };

    public void DetectHardware(){
        File file_goodix =new  File("/dev/"+GOODIX_FP);
        File file_marry =new  File("/dev/"+MARRY_FP);
        File file_marry_tee =new  File("/dev/"+MARRY_FP_TEE);
        File file_silead =new  File("/dev/"+SILEAD_FP);
        if(file_goodix.exists() || file_marry.exists() || file_marry_tee.exists() || file_silead.exists()){
            tv_status.setText(getString(R.string.test_fp_hw_available));
            handler.sendEmptyMessageDelayed(MSG_FINGERPRINT_TEST_SUCCESS,500);
            Log.d(TAG,"Fingerprint Hardware is detected !");
        }else {
            tv_status.setText(getString(R.string.test_fp_hw_unavailable));
            handler.sendEmptyMessageDelayed(MSG_FINGERPRINT_TEST_FAILED,500);
            Log.d(TAG, "Fingerprint Hardware is not detected !");
        }
    }

    @Override
    public void startTest() {
        DetectHardware();
        Log.d(TAG,"start to test FingerprintTest");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        startTest();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 手动结束测试返回
     */

    @Override
    public void onBackPressed() {
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