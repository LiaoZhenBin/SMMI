package com.asus.atd.smmitest.testcase;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.view.KeyEvent;
//zhangtao@wind-mobi.com 2016/8/12 add begain
import android.widget.Toast;
//zhangtao@wind-mobi.com 2016/8/12 add end
import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by heliguang on 2016/1/15.
 */
public class CompassTesting extends BaseActivity implements OnClickListener{
    private ModuleManager mModuleManager;
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    private static final String mModuleName = "ECompass Test";

    private Button btn_pass;
    private Button btn_fail;
    private ImageView imageViewCompass;
    private SensorManager mSensorManager;
    private float currentDegree = 0f;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_compass);
		this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        btn_fail = (Button) findViewById(R.id.btn_fail);
        btn_pass = (Button) findViewById(R.id.btn_pass);
        imageViewCompass = (ImageView) findViewById(R.id.imageViewCompass);
    }

    @Override
    protected void setListener() {
        btn_fail.setOnClickListener(this);
        btn_pass.setOnClickListener(this);
    }

    @Override
    protected void init() {
        {
            mModuleManager = ModuleManager.getInstance(this);
        }
    }

    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //zhangtao@wind-mobi.com 2016/8/10 modify begain
        // Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        Sensor magn=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor acce=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(listener, magn, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(listener,acce,SensorManager.SENSOR_DELAY_GAME);
        //zhangtao@wind-modi.com 2016/8/10 modify end
        //zhangtao@wind-mobi.com 2016/8/12 modify begain
        Toast.makeText(CompassTesting.this,getResources().getString(R.string.friendly_remind),Toast.LENGTH_SHORT).show();
        //zhangtao@wind-mobi.com 2016/8/12 modify end
    }
	

    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(listener);
    }

    public void setTestResult(int code,int count){
        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("ECompass Test")){
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
            Log.d(TAG, "BluetoothTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode,mModuleName);
            setResult(resultCode);
            finish();
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_pass:
                mSucCount++;
                setTestResult(Constants.TEST_PASS,mSucCount);
                finishActivity(Constants.TEST_PASS);
                break;
            case R.id.btn_fail:
                mFailCount++;
                setTestResult(Constants.TEST_FAILED,mFailCount);
                finishActivity(Constants.TEST_FAILED);
                break;
            default:
                break;
        }
    }
    
    SensorEventListener listener = new SensorEventListener() {

            //zhangtao@wind-mobi.com 2016/8/12 modify begain
            float[] acceValues = new float[3];
            float[] magnValues = new float[3];
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //zhangtao@wind-mobi.com modify 2016/8/10 begain
            // get the angle around the z-axis rotated
            /*  float degree = Math.round(sensorEvent.values[0]);

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            imageViewCompass.startAnimation(ra);
            currentDegree = -degree;*/
            //float[] acceValues = new float[3];
            //float[] magnValues = new float[3];
            //zhangtao@wind-mobi.com 2016/8/12 modify end
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acceValues = sensorEvent.values.clone();

            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magnValues = sensorEvent.values.clone();
            }
            float [] a=new float[9];
            float [] b=new float[3];
            SensorManager.getRotationMatrix(a, null, acceValues, magnValues);
            SensorManager.getOrientation(a,b);
            float rotate=-(float)Math.toDegrees(b[0]);
            if(Math.abs(rotate-currentDegree)>1){
                RotateAnimation animation=new RotateAnimation(currentDegree,rotate,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation.setFillAfter(true);
				//zhangtao@wind-mobi.com 2016/8/12 modify begain
				animation.setDuration(250);
				//zhangtao@wind-mobi.com 2016/8/12 modify end
                imageViewCompass.startAnimation(animation);
                //zhangtao@wind-mobi.com 2016/8/12 modify begain
                //currentDegree = - rotate;
                currentDegree = rotate;
                //zhangtao@wind-mobi.com 2016/8/12 modify end
            }
            //zhangtao@wind-mobi.com modify 2016/8/10 end 

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }

}
