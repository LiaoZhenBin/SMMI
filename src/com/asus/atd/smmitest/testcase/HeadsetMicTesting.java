package com.asus.atd.smmitest.testcase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by liuyang on 2016/1/14.
 */
public class HeadsetMicTesting extends BaseActivity {
    private TextView tv_play;
    private static final String mModuleName = "Headset Mic Test";
    private ModuleManager mModuleManager;
    private static final int HSETMIC_START_RECORD = 100;
    private static final int HSETMIC_START_PLAY = 101;
    private static final int HSETMIC_STOP = 105;

    private String fileName = null;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private static int mSucCount = 0;
    private static int mFailCount = 0;
    public static boolean HEADSET_ON;

    /**
     * 设置消息处理
     */
    private final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //开始录音
                    case HSETMIC_START_RECORD:
                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setOutputFile(fileName);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        try {
                            mRecorder.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mRecorder.start();
                        break;
                    //开始播放
                    case HSETMIC_START_PLAY:
                        mRecorder.stop();
                        tv_play.setText(R.string.text_headsetmic_play);
                        mRecorder.release();
                        mRecorder = null;
                        mPlayer = new MediaPlayer();
                        try {
                            mPlayer.setDataSource(fileName);
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    //停止播放，弹出结果对话框
                    case HSETMIC_STOP:
                        mPlayer.release();
                        mPlayer = null;
                        showPassFailDialog("PASS", "FAIL");
                        break;
                    default:
                        break;
                }

            }
        };

    /**
     * 播放结束后，需手动执行对话框返回结果
     * @param paramString1
     * @param paramString2
     */
    private void showPassFailDialog(String paramString1, String paramString2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_juge);
        //使dialog一直获取焦点
        builder.setCancelable(false);
        builder.setPositiveButton(paramString1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(HeadsetMicTesting.this, "PASS", Toast.LENGTH_SHORT).show();
                mSucCount++;
                Log.d(TAG, "mSucCount:" + mSucCount);
                setTestResult(Constants.TEST_PASS, mSucCount);
                finishActivity(Constants.TEST_PASS);
                Log.d(TAG, "Headset Mic test is pass");
            }
        });
        builder.setNegativeButton(paramString2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(HeadsetMicTesting.this, "FAIL", Toast.LENGTH_SHORT).show();
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
                Log.d(TAG, "Headset Mic test is failed");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 检测无耳机设备时的报错对话框
     */
    private void showErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle(R.string.dialog_headsetmic_nofound);
        builder.setMessage(R.string.dialog_headsetmic_in);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //huangzhijian@wind-mobi.com modify 20161018 start
                startTest();
//                mFailCount++;
//                setTestResult(Constants.TEST_FAILED, mFailCount);
//                finishActivity(Constants.TEST_FAILED);
                //huangzhijian@wind-mobi.com modify 20161018 end
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override

    protected void setContentView() {
        setContentView(R.layout.activity_testing_headsetmic);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        tv_play = (TextView) findViewById(R.id.tv_hsmic_play);
    }

    /**
     * 开始测试
     */
    @Override
    public void startTest() {
        AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        HEADSET_ON=localAudioManager.isWiredHeadsetOn();
        //检测到耳机则进行消息处理
        if (HEADSET_ON) {
            mHandler.sendEmptyMessage(HSETMIC_START_RECORD);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        mHandler.sendEmptyMessage(HSETMIC_START_PLAY);
                        Thread.sleep(3000);
                        mHandler.sendEmptyMessage(HSETMIC_STOP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        //检测无耳机设备，返回出错对话框提示
        else {
            showErrorDialog();
        }
    }




    @Override
    protected void init() {
        mModuleManager= ModuleManager.getInstance(this);
    }

    @Override
    protected void setListener() {

    }
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(mModuleName);
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/Headsetmictest.3gp";
        startTest();
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
     * 返回测试结果
     * @param code
     * @param count
     */
    public void setTestResult(int code, int count) {
        for (TestCase module : mModuleManager.mTestModule) {
            if (module.getEnName().equals(mModuleName)) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }
}