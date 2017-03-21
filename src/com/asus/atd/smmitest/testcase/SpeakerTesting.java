package com.asus.atd.smmitest.testcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by zhangtao on 2016/1/13.
 */
public class SpeakerTesting extends BaseActivity {

    private ModuleManager mModule;
    private String mModuleName = "Speaker Test";
    private static final int MSG_PASS = 100;
    private static final int MSG_FAIL = 101;
    private static final int THREAD_START=102;
    private MediaPlayer mediaPlayer;
    private static int mSucCount = 0;
    private static int mFailCount = 0;
   private Boolean FLAG=false;


    //与外部交互
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case THREAD_START:
                   if(FLAG=false) {
                       mediaPlayer.setVolume(0, 100);
                   } break;

                case MSG_FAIL:
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);

                    finishActivity(Constants.TEST_FAILED);
                    break;
                case MSG_PASS:
                    mSucCount++;

                    setTestResult(Constants.TEST_PASS, mSucCount);

                    finishActivity(Constants.TEST_PASS);
                    break;
                default:
                    break;

            }

        }


    };


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_test_speaker);
      //  setTitle("Speaker Test");
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }


    //初始化

    @Override
    protected void init() {
        mModule = ModuleManager.getInstance(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.check_spk_l_r);
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

    public void setTestResult(int code, int count) {
        for (TestCase module : mModule.mTestModule) {
            if (module.getEnName().equals(mModuleName)) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    @Override
    public void startTest() {
playMusic();
 FLAG=false;

    }

    private void playMusic() {

        new Thread(){
            @Override
            public void run() {
              handler.sendEmptyMessageDelayed(THREAD_START,2000);
            }
        }.start();


        mediaPlayer.setVolume(100,0);

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {

            }
        });

        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //liaozhenbin@wind-mobi.com 20160721 add begin
                new AlertDialog.Builder(SpeakerTesting.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setTitle("Test Finished").setMessage("Test finished,Please make a judgement!").setCancelable(false).setNegativeButton("FAIL", new DialogInterface.OnClickListener() {
                    //liaozhenbin@wind-mobi.com 20160721 add end
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        handler.sendEmptyMessageDelayed(MSG_FAIL, 500);

                    }
                }).setPositiveButton("PASS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                handler.sendEmptyMessageDelayed(MSG_PASS, 500);

                            }
                        }

                ).show();


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
      if(mediaPlayer!=null){
          mediaPlayer.release();
          mediaPlayer=null;
      }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        startTest();
    }


    @Override
    public void onBackPressed() {
        FLAG=true;

        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
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
