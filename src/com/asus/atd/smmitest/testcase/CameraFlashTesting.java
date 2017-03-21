package com.asus.atd.smmitest.testcase;


import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by zhangtao on 2016/1/12.
 */
public class CameraFlashTesting extends BaseActivity {

    private Button btnFail, btnPass;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private static final int MSG_PASS = 100;
    private static final int MSG_FAIL = 101;
    private ModuleManager mModule;
    private String moduleName = "CameraFlash Test";

    private static int mSucCount = 0;
    private static int mFailCount = 0;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_camera_flash);
       // setTitle("CameraFlash Test");
        this.getActionBar().hide();
    }


    //与外部交互，实现消息的处理与发送
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PASS:
                    mSucCount++;
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                    break;
                case MSG_FAIL:
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);

                    finishActivity(Constants.TEST_FAILED);
                    default:
                        break;
            }

        }
    };

    public void setTestResult(int code, int count) {
        for (TestCase module : mModule.mTestModule) {
            if (module.getEnName().equals(moduleName)) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    @Override
    protected void findViewById() {
        btnFail = (Button) findViewById(R.id.fail_btn_camera);
        btnPass = (Button) findViewById(R.id.pass_btn_camera);
    }

    @Override
    protected void setListener() {
        btnFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessageDelayed(MSG_FAIL,500);

            }
        });
        btnPass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                handler.sendEmptyMessageDelayed(MSG_PASS,500);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);

    }

    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, moduleName);
            finish();

        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, moduleName);
            setResult(resultCode);
            finish();
        }
    }

    @Override
    protected void init() {
        mModule = ModuleManager.getInstance(this);
        mCamera = Camera.open();
        mCamera.startPreview();
        parameters = mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
    }
    @Override
    protected void onPause() {
        if(mCamera != null){
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
