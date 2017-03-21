package com.asus.atd.smmitest.testcase;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;


/**
 * Created by liuyang on 2016/1/14.
 */
public class LightSensorTesting extends BaseActivity implements SensorEventListener{
    private static final float VALUE_MAX=300F;
    private static final float VALUE_MIN=5F;
    private static int mSucCount=0;
    private static int mFailCount = 0;
    private static final int MSG_PASS = 101;
    private static final int MSG_FAIL = 102;
    private ModuleManager mModuleManager;
    private static final String mModuleName="LightSensor Test";

    private TextView tv_info;
    private Button btn_fail;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mTAG=true;
    private boolean mTAG1=false;
    private boolean mTAG2=false;
    private static boolean mflag=false;

    /**
     * 设置传感器类型：光线传感器，注册监听
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        btn_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
            }
        });
    }

    /**
     * 设置感光处理事件
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mTAG) {
            float[] values = event.values;
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_LIGHT&&mTAG) {
                if ((values[0] > VALUE_MAX)) {
                    image1.setVisibility(View.VISIBLE);
                    tv_info.setText(R.string.tv_lsensor_awayinfo);
                    mTAG1 = true;
                    mflag=false;
                    Log.d(TAG, "LightSensor 1");
                }
                if ((values[0] < VALUE_MIN)&&mTAG1) {
                    mTAG1=false;
                    tv_info.setText(R.string.tv_lsensor_closeinfo);
                    image2.setVisibility(View.VISIBLE);
                    mTAG2 = true;
                    mflag=false;
                    Log.d(TAG, "LightSensor 2");

                }
                if ((values[0] > VALUE_MAX)&&mTAG2) {
                    tv_info.setText(R.string.tv_lsensor_success);
                    image3.setVisibility(View.VISIBLE);
                    mTAG=false;
                    mflag=true;
                    Log.d(TAG, "LightSensor 3");
                }
            }

        }
        if(mflag) {
            mflag=false;
            mSucCount++;
            setTestResult(Constants.TEST_PASS, mSucCount);
            setResult(Constants.TEST_PASS);
            finishActivity(Constants.TEST_PASS);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_lightsensor);
    }

    @Override
    protected void findViewById() {
        tv_info=(TextView) findViewById(R.id.tv_move_info);
        image1=(ImageView) findViewById(R.id.image1_lsensor);
        image2=(ImageView) findViewById(R.id.image2_lsensor);
        image3=(ImageView) findViewById(R.id.image3_lsensor);
        btn_fail=(Button) findViewById(R.id.btn_lsensor_fail);
    }


    @Override
    protected void init() {
        mModuleManager= ModuleManager.getInstance(this);
        setTitle(mModuleName);

    }

    @Override
    protected void setListener() {

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

    /**
     *清除数据
     */
    private void clearData(){
        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
        tv_info.setText(R.string.tv_lsensor_closeinfo);
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        clearData();
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

    /**
     * 测试成功则自动返回
     * 否则手动返回
     */
    @Override
    public void onBackPressed() {
        mFailCount++;
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED, mModuleName);
        setTestResult(Constants.TEST_FAILED, mFailCount);
        setResult(Constants.TEST_FAILED);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
