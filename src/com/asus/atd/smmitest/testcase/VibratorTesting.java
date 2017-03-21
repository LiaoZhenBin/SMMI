package com.asus.atd.smmitest.testcase;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by liuyang on 2016/1/13.
 */
public class VibratorTesting extends BaseActivity {
    private static int mSucCount=0;
    private static int mFailCount=0;
    private ModuleManager mModuleManager;
    private Vibrator mVibrator01;
    private static final String mModuleName="Vibrator Test";
    Button btn_pass;
    Button btn_fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(mModuleName);
        mVibrator01=(Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        Switch switchTest=(Switch) findViewById(R.id.sw_test);
		//liuyang@wind-mobi.com modify 2016/3/10 begin
		VibratorTesting.this.mVibrator01.vibrate(new long[]{100L, 100L, 100L, 1000L}, 0);
        switchTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    VibratorTesting.this.mVibrator01.vibrate(new long[]{100L, 100L, 100L, 1000L}, 0);
                    return;
                } else {
                    VibratorTesting.this.mVibrator01.cancel();
                }
            }
        });
		btn_pass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSucCount++;
                setTestResult(Constants.TEST_PASS, mSucCount);
                VibratorTesting.this.mVibrator01.cancel();
                finishActivity(Constants.TEST_PASS);
                        }
        });
        btn_fail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                VibratorTesting.this.mVibrator01.cancel();
                finishActivity(Constants.TEST_FAILED);
            }
        });
		//liuyang@wind-mobi.com modify 2016/3/10 end
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_vibrator);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        if(findViewById(R.id.btn_pass)==null&&findViewById(R.id.btn_fail)==null){
            View v = LayoutInflater.from(this).inflate(R.layout.layout_show_picture, (ViewGroup) this.getWindow().getDecorView());
            btn_pass=(Button)findViewById(R.id.btn_pass);
            btn_fail=(Button)findViewById(R.id.btn_fail);
        }
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void init() {
        mModuleManager= ModuleManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void startTest() {
        super.startTest();
    }

    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, mModuleName);
            finish();
        }else{
            SaveTestResult.getInstance(this).saveResult(resultCode,mModuleName);
            setResult(resultCode);
            finish();
        }
    }

    public void setTestResult(int code,int count){
        for(TestCase module:mModuleManager.mTestModule){
            if(module.getEnName().equals(mModuleName)) {
                if (code == Constants.TEST_PASS) {
                    module.setSuccessCount(count);
                } else {
                    module.setFailCount(count);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mFailCount++;
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED, mModuleName);
        setTestResult(Constants.TEST_FAILED, mFailCount);
        setResult(Constants.TEST_FAILED);
        VibratorTesting.this.mVibrator01.cancel();
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
