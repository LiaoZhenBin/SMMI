package com.asus.atd.smmitest.testcase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import android.view.KeyEvent;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

import java.io.IOException;

/**
 * Created by heliguang on 2016/1/18.
 */
public class ReceiverTesting extends BaseActivity {

    private TextView tv_tip;
    private static final int TIME_OUT = 1001;
    //zhangtao@wind-mobi.com 2016/8/1 modify begain
	//private Boolean FLAG = false;
    private boolean flag = false;
    //zhangtao@wind-mobi.com 2016/8/1 modify end
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    private static final String mModuleName = "Receiver Test";
    MediaPlayer mp;
    private Uri mUri;
    PowerManager.WakeLock mWakeLock;
    private ModuleManager mModule ;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case TIME_OUT:
                    //zhangtao@wind-mobi.com 2016/8/1 modify begain
					/* if(mp != null){
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                    showAlertDialog();*/
                    if (flag ==false) {
                        showAlertDialog();
                    }
                    //zhangtao@wind-mobi.com 2016/8/1 modify end
                    break;
            }

        }


    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_receiver);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        tv_tip = (TextView) findViewById(R.id.tv_tip);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Receiver_Test");
    }

    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        startTest();
    }

    @Override
    public void startTest() {
        //zhangtao@wind-mobi.com 2016/8/1 add begain
        flag = false;
        //zhangtao@wind-mobi.com 2016/8/1 add end
        tv_tip.setText("Playing...");
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mp.setScreenOnWhilePlaying(true);
        try{
            mp.setDataSource(this, mUri);
            mp.prepare();
        }catch(IOException e){
            e.printStackTrace();
        }

        mp.setLooping(true);
        mp.setVolume(1.0f, 1.0f);
        mp.start();

        new Thread(){
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(TIME_OUT,4000);
            }
        }.start();
    }

    public void showAlertDialog() {
        //liaozhenbin@wind-mobi.com 20160721 add begin
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Test Finished").setMessage("Test finished, Please make a judgement");
        //liaozhenbin@wind-mobi.com 20160721 add end
        builder.setCancelable(false);
        setPositiveButton(builder);
        setNegativeButton(builder)
                .create()
                .show();
    }

    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
        return builder.setPositiveButton("PASS", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                mSucCount++;
                setTestResult(Constants.TEST_PASS, mSucCount);
                finishActivity(Constants.TEST_PASS);
            }
        });
    }

    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        return builder.setNegativeButton("FAIL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
            }
        });
    }

    @Override
    protected void onDestroy() {
        //zhangtao@wind-mobi.com 2016/8/1 add begain
        flag=true;
        //zhangtao@wind-mobi.com 2016/8/1 add end
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }

    public void setTestResult(int code,int count){
        for (TestCase module:mModule.mTestModule){
            if (module.getEnName().equals("Receiver Test")){
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
            Log.d(TAG, "ReceiverTest finished ,send broadcast to run the next test module");
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
