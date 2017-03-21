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
import android.widget.TextView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

//import java.io.IOException;

/**
 * Created by luyuan on 2016/1/15.
 */
public class ProximityTesting extends BaseActivity implements SensorEventListener {

    private static final String mModuleName = "Proximity Test";
    private ModuleManager mModuleManager;
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    private boolean flag = true;


    TextView tv_title;
    TextView tv_line;

    TextView tv_near;
    TextView tv_near1;
    TextView tv_away;

    ImageView im_right_near;
    ImageView im_right_near1;
    ImageView im_right_away;

    Button btn_fail;

    SensorManager sm;
    Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setTitle(getString(R.string.proximity_test));
        //传感器管理对象的引用的实例化
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //先把对勾的imageview隐藏掉
        im_right_near.setVisibility(View.INVISIBLE);
        im_right_away.setVisibility(View.INVISIBLE);
        im_right_near1.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);
        btn_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
			mFailCount++;
            setTestResult(Constants.TEST_FAILED, mFailCount);
            finishActivity(Constants.TEST_FAILED);
            }
        });
    }

    @Override
    protected void setContentView() {
//        tv_title.setText(R.string.title_proximity_near);
        setContentView(R.layout.activity_testing_proximity);
    }

    @Override
    protected void findViewById() {
        tv_title = (TextView) findViewById(R.id.title_proximity);
        tv_near = (TextView) findViewById(R.id.text_near);
        tv_near1 = (TextView) findViewById(R.id.text_near1);
        tv_away = (TextView) findViewById(R.id.text_away);
        im_right_near = (ImageView) findViewById(R.id.image_near);
        im_right_near1 = (ImageView) findViewById(R.id.image_near1);
        im_right_away = (ImageView) findViewById(R.id.image_away);
        btn_fail = (Button) findViewById(R.id.btn_fail);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //解除对传感器监听的注册
        sm.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        //注册距离传感器的事件监听
        sm.registerListener(this, mSensor, SensorManager.SENSOR_PROXIMITY);
        flag = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isAway = true;
    private boolean isFirstNear = true;

    //传感器感应距离事件变化
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
                //tv_away.setText("Distance" + values[0]*3);
                values[0] = values[0] * 3;

                if (values[0] < 2.0||!isFirstNear) {

                    if (values[0] < 2.0) {
                        if (isFirstNear) {
                            im_right_near.setVisibility(View.VISIBLE);
                            tv_title.setText(R.string.title_proximity_away);
                            isFirstNear = false;
                        } else if (!isFirstNear && !isAway) {
                            im_right_near1.setVisibility(View.VISIBLE);
                            tv_title.setText(R.string.tv_proximity_success);
                            if (flag) {
                                ++mSucCount;
                            }
                            flag = false;
                            isAway=true;
                            isFirstNear=true;
                            setTestResult(Constants.TEST_PASS, mSucCount);
                            handler.sendEmptyMessageDelayed(0, 500);
                        }

                    } else {
                        im_right_away.setVisibility(View.VISIBLE);
                        tv_title.setText(R.string.title_test_proximity);
                        isAway=false;
                    }
                }
            } else {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
            }
        }
    }


    //处理测试成功后的结束该activity的任务
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //mSucCount++;
                // setTestResult(Constants.TEST_PASS, mSucCount);
                finishActivity(Constants.TEST_PASS);

            }

        }
    };

    /**
     * 设置测试返回信息
     *
     * @param code
     * @param count
     */

    public void setTestResult(int code, int count) {
        for (TestCase module : mModuleManager.mTestModule) {
            if (module.getEnName().equals("Proximity Test")) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    /**
     * 测试正常结束返回
     * 根据是单项测试/自动测试：
     * 自动测试 --- MainManager.getInstance().sendFinishBroadcast()
     * 单项测试 --- 更新测试结果
     *
     * @param resultCode
     */


    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, mModuleName);
            finish();
            //Log.d(TAG,"BluetoothTest finished ,send broadcast to run the next test module");
        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModuleName);
            setResult(resultCode);

            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        mSensor = sensor;

    }
}
