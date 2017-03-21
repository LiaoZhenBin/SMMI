package com.asus.atd.smmitest.testcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
 * Created by liaozhenbin on 2016/1/12.
 */
//liaozhenbin@wind-mobi.com 20160112 mod begin
//实现BroadMic Test模块
public class BroadMicTesting extends BaseActivity {
    private static final String mModuleName = "BroadMic Test";
    private ModuleManager mModuleManager;
    private TextView tv_broad_mic;
    private static final int MSG_BM_START_RECORD = 100;
    private static final int MSG_BM_START_PLAY = 101;
    private static final int MSG_BM_STOP = 105;
    //zhangtao@wind-mobi.com 2016/8/1 add begain
    private boolean flag = false;
    //zhangtao@wind-mobi.com 2016/8/1 add end
    private String fileName = null;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    private static int mSucCount = 0;
    private static int mFailCount = 0;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage( Message msg) {
            //zhangtao@wind-mobi.com 2016/8/1 modify begain
            /*switch (msg.what) {

                case MSG_BM_START_RECORD:
                    //开始录音
                    mRecorder = new MediaRecorder();
                    //设置音频类型
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    //设置保存模式
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(fileName);
                    //设置编码格式
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        //录音器准备
                        mRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mRecorder.start();
                    break;
                case MSG_BM_START_PLAY:
                    mRecorder.stop();
                    tv_broad_mic.setText("Play...");
                    //释放录音器
                    mRecorder.release();
                    //把录音器置为空
                    mRecorder = null;
                    //播放录音
                    mPlayer = new MediaPlayer();

                    try {
                        mPlayer.setDataSource(fileName);
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_BM_STOP:
                    //释放播放器
                    mPlayer.release();
                    //把播放器置为空
                    mPlayer = null;
                    showPassFailDialog("PASS", "FAIL");
                    break;
                default:
                    break;
            }*/
            if (flag == false) {

                switch (msg.what) {

                    case MSG_BM_START_RECORD:
                        //开始录音
                        mRecorder = new MediaRecorder();
                        //设置音频类型
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        //设置保存模式
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setOutputFile(fileName);
                        //设置编码格式
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        try {
                            //录音器准备
                            mRecorder.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mRecorder.start();
                        break;
                    case MSG_BM_START_PLAY:
                        mRecorder.stop();
                        tv_broad_mic.setText("Play...");
                        //释放录音器
                        mRecorder.release();
                        //把录音器置为空
                        mRecorder = null;
                        //播放录音
                        mPlayer = new MediaPlayer();

                        try {
                            mPlayer.setDataSource(fileName);
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MSG_BM_STOP:
                        //释放播放器
                        mPlayer.release();
                        //把播放器置为空
                        mPlayer = null;
                        showPassFailDialog("PASS", "FAIL");
                        break;
                    default:
                        break;
                }
            }
            //zhangtao@wind-mobi.com 2016/8/1 modify end
        }
    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_broadmic);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        tv_broad_mic = (TextView) findViewById(R.id.tv_broad_mic);
    }

    @Override
    protected void setListener() {

    }

    /**
     * 显示成功失败选择对话框
     *
     * @param paramString1
     * @param paramString2
     */
    private void showPassFailDialog(String paramString1, String paramString2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Test Finished");
        builder.setMessage("Test finished.Please make a judgement.");
        //使dialog一直获取焦点
        builder.setCancelable(false);
        builder.setPositiveButton(paramString1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSucCount++;
                Log.d(TAG, "mSucCount:" + mSucCount);
                setTestResult(Constants.TEST_PASS, mSucCount);
                finishActivity(Constants.TEST_PASS);
                Log.d(TAG, "BroadMic test is pass");
            }
        });
        builder.setNegativeButton(paramString2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
                Log.d(TAG, "BroadMic test is failed");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
            Log.d(TAG, "BroadTest finished ,send broadcast to run the next test module");
        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModuleName);
            setResult(resultCode);
            finish();
        }
    }

    /**
     * 设置测试返回信息
     *
     * @param code
     * @param count
     */
    public void setTestResult(int code, int count) {
        for (TestCase module : mModuleManager.mTestModule) {
            if (module.getEnName().equals("BroadMic Test")) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);
        this.setTitle(mModuleName);
    }

    /**
     * 用来捕捉手机键盘被按下的事件，返回true表示已经完整处理这个事件，并不希望其他的回调方法再次进行处理
     *
     * @param paramInt
     * @param paramKeyEvent
     * @return
     */
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //把底下的button移除
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        //设置sdcard的路径
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/boardmictest.3gp";
        startTest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //zhangtao@wind-mobi.com 2016/8/1 add begain
        flag = true;
        //zhangtao@wind-mobi.com 2016/8/1 add end
    }

    @Override
    public void startTest() {
        //zhangtao@wind-mobi.com 2016/8/1 add begain
        flag = false;
        //zhangtao@wind-mobi.com 2016/8/1 add end
        handler.sendEmptyMessage(MSG_BM_START_RECORD);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //3秒录音时间
                    Thread.sleep(3000);
                    handler.sendEmptyMessage(MSG_BM_START_PLAY);
                    //3秒播放时间
                    Thread.sleep(3000);
                    handler.sendEmptyMessage(MSG_BM_STOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
//liaozhenbin@wind-mobi.com 20160112 mod end