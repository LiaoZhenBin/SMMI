package com.asus.atd.smmitest.testcase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;


/**
 * Created by liuyang on 2016/1/12.
 */

public class GSensorTesting extends BaseActivity {
    private SensorManager mSensorManager;
    private FrameLayout mLinearLayout;
    private GSensorView mGSensorView;
    private ModuleManager mModuleManager;
    private static final String mModuleName = "GSensor Test";
    Button btnfail;

    private static int mSucCount=0;
    private static int mFailCount=0;
    private boolean Flag=true;

    private SensorEventListener listener = new SensorEventListener() {
        /**
         * 传感器监听：重力加速度改变（重力加速度默认值9.8），刷新UI
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {
            float xValue = event.values[0];
            float yValue = event.values[1];
            float zValue = event.values[2];

            if (xValue < -9.8) {
                mGSensorView.TagX = true;
                //刷新View
                mGSensorView.invalidate();

            } else if (xValue > 9.8) {
                mGSensorView.TagX1 = true;
                mGSensorView.invalidate();

            }

            if (yValue > 9.8) {
                mGSensorView.TagY = true;
                //刷新View
                mGSensorView.invalidate();

            } else if (yValue < -9.8) {
                mGSensorView.TagY1 = true;
                mGSensorView.invalidate();
            }

            if (zValue > 9.8) {
                mGSensorView.TagZ = true;
                //刷新View
                mGSensorView.invalidate();

            } else if (zValue < -9.8) {
                mGSensorView.TagZ1 = true;
                mGSensorView.invalidate();
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setTitle(mModuleName);
        this.getActionBar().hide();
        btnfail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
            }
        });
        setListener();
    }


    private class GSensorView extends View {
        private boolean TagX = false;
        private boolean TagX1 = false;
        private boolean TagY = false;
        private boolean TagY1 = false;
        private boolean TagZ = false;
        private boolean TagZ1 = false;

        public GSensorView(Context context) {
            super(context);
        }

        public GSensorView(Context context, AttributeSet attr) {
            super(context, attr);
        }

        /**画坐标和文字
         * 画6个黑球
         * @param canvas
         */
        protected void onDraw(Canvas canvas) {
            int heigh = getMeasuredHeight();
            int width = getMeasuredWidth();

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            paint.setTextSize(40);

            paint.setColor(Color.RED);
            canvas.drawLine(60, heigh / 2, width - 60, heigh / 2, paint);
            canvas.drawText("X-", 70, heigh / 2 - 30, paint);
            canvas.drawText("X+", width - 90, heigh / 2 - 30, paint);

            paint.setColor(Color.YELLOW);
            canvas.drawLine(width / 2, heigh - 60, width / 2, 60, paint);
            canvas.drawText("Y-", width / 2 + 30, heigh - 90, paint);
            canvas.drawText("Y+", width / 2 + 30, 90, paint);

            paint.setColor(Color.BLUE);
            canvas.drawText("Z+", 200, heigh - 180, paint);
            canvas.drawText("Z-", width - 160, 220, paint);

            paint.setColor(Color.BLUE);
            canvas.drawLine(width / 2, heigh / 2, 150, heigh - 150, paint);
            PathEffect effect = new DashPathEffect(new float[]{10, 10, 10, 10}, 1);
            paint.setPathEffect(effect);
            Path path = new Path();
            path.moveTo(width / 2, heigh / 2);
            path.lineTo(width - 150, 150);
            canvas.drawPath(path, paint);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(40, heigh / 2, 20, paint);
            canvas.drawCircle(width - 40, heigh / 2, 20, paint);
            canvas.drawCircle(width / 2, 30, 20, paint);
            canvas.drawCircle(width / 2, heigh - 30, 20, paint);
            canvas.drawCircle(150, heigh - 150, 20, paint);
            canvas.drawCircle(width - 150, 150, 20, paint);

            if (TagX) {
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(40, heigh / 2, 20, paint);
            }
            if (TagX1) {
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(width - 40, heigh / 2, 20, paint);
            }

            if (TagY) {
                paint.setColor(Color.YELLOW);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(width / 2, 30, 20, paint);
            }
            if (TagY1) {
                paint.setColor(Color.YELLOW);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle((width / 2), heigh - 30, 20, paint);
            }

            if (TagZ) {
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(150, heigh - 150, 20, paint);
            }
            if (TagZ1) {
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(width - 150, 150, 20, paint);
            }
            if (mGSensorView.TagX && mGSensorView.TagX1 && mGSensorView.TagY && mGSensorView.TagY1 &&
                    mGSensorView.TagZ && mGSensorView.TagZ1) {
                while (Flag) {
                    mSucCount++;
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                    Flag=false;
                }

            }
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_gsensor);
    }

    @Override
    protected void findViewById() {
        mLinearLayout = (FrameLayout) findViewById(R.id.llGSensor);
        btnfail = (Button) findViewById(R.id.btn_ls_fail);
    }

    /**把重力感应坐标添加到布局里
     * 传感器类型：加速度传感器
     */

    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);
        mGSensorView = new GSensorView(this);
        mLinearLayout.addView(mGSensorView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }

    @Override
    public void startTest() {
        super.startTest();
    }


    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, mModuleName);
            finish();
        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModuleName);
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

    /**
     * 手动结束测试返回
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}

