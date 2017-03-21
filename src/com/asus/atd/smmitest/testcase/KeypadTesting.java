package com.asus.atd.smmitest.testcase;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by zhangtao on 2016/1/11.
 */

public class KeypadTesting extends BaseActivity {

    private TextView tvTitle;
    private TextView volumeUp;
    private TextView volumeDown;
    private ImageView imageUp;
    private ImageView imageDown;
    private Button btnFail;
    private ModuleManager mModuleName;
    private static int mFailCount = 0;
    private static int mSusCount = 0;
    private static final int MSG_PASS = 1;
    private static final int MSG_FAIL = 2;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_keypad);
        //setTitle("Keypad Test");
Toast.makeText(KeypadTesting.this,"Please do it according to the title!",Toast.LENGTH_SHORT).show();
        this.getActionBar().hide();

    }


    @Override
    protected void findViewById() {

        volumeDown = (TextView) findViewById(R.id.tv2_test_keypad);
        volumeUp = (TextView) findViewById(R.id.tv1_test_keypad);
        tvTitle = (TextView) findViewById(R.id.tv_title_keypad);
        imageUp = (ImageView) findViewById(R.id.image_up_keypad);
        imageDown = (ImageView) findViewById(R.id.image2_up_keypad);
        btnFail = (Button) findViewById(R.id.btn_test_keypad);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PASS:
                    mSusCount++;
                    setTestResult(Constants.TEST_PASS, mSusCount);
                    finishActivity(Constants.TEST_PASS);
                    break;
                case MSG_FAIL:
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                    break;
                default:
                    break;
            }


        }
    };


    @Override
    protected void setListener() {

        btnFail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:

                        mHandler.sendEmptyMessageDelayed(MSG_FAIL,500);

                        break;

                    default:
                        break;
                }


                return false;
            }
        });

    }

    public void setTestResult(int code, int count) {
        for (TestCase module : mModuleName.mTestModule) {
            if (module.getEnName().equals("Keypad Test")) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    private void clearData() {

        imageDown.setVisibility(View.INVISIBLE);
        imageUp.setVisibility(View.INVISIBLE);
        tvTitle.setText("Please press Volume Up button.");
        //volumeDown.setTextColor(Color.LTGRAY);
        //volumeUp.setTextColor(Color.LTGRAY);
        flag = true;
    }


    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, mModule);
            finish();

        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModule);
            setResult(resultCode);
            finish();
        }
    }

    @Override
    protected void init() {

        mModuleName = ModuleManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        clearData();
    }

    @Override
    public void onBackPressed() {

        mFailCount++;
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED, mModule);
        setTestResult(Constants.TEST_FAILED, mFailCount);
        setResult(Constants.TEST_FAILED);
        super.onBackPressed();
    }

    private Boolean flag = true;
    private String mModule = "Keypad Test";
    private int count = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (flag == true) {
                    changeData1();
                    count++;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (flag == false) {
                    changeData2();
                    count++;
                    mHandler.sendEmptyMessageDelayed(MSG_PASS, 500);
                }
                break;
            default:
                if (count < 2) {
                    Toast.makeText(KeypadTesting.this, tvTitle.getText().toString(), Toast.LENGTH_SHORT);
                }
                break;
        }
        return true;
    }

    private void changeData1() {
        tvTitle.setText(getResources().getString(R.string.title2_test_keypad));
        imageUp.setVisibility(View.VISIBLE);
        //volumeUp.setTextColor(Color.WHITE);
        flag = false;
    }

    private void changeData2() {
        tvTitle.setText(getResources().getString(R.string.title3_test_keypad));
        imageDown.setVisibility(View.VISIBLE);
        //volumeDown.setTextColor(Color.WHITE);
        flag = true;
    }


}
