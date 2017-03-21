package com.asus.atd.smmitest.testcase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

/**
 * Created by liaozhenbin on 2016/1/12.
 */
//liaozhenbin@wind-mobi.com 20160112 mod start
//添加HeadsetTesting 模块
public class HeadsetTesting extends BaseActivity {
    private static final String mModuleName = "Headset Test";
    private ModuleManager mModuleManager;
    private TextView tv_headset;
    private AudioManager localAudioManager;
    //zhangtao@wind-mobi.com 2016/8/1 add begain
    private boolean flag = false;
    //zhangtao@wind-mobi.com 2016/8/1 add end
    private static int mSucCount = 0;
    private static int mFailCount = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //zhangtao@wind-mobi.com 2016/8/1 modify begain
                    //showPassFailDialog("PASS", "FAIL");
                    if (flag == false) {
                        showPassFailDialog("PASS", "FAIL");
                    }
                    //zhangtao@wind-mobi.com 2016/8/1 modify end
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_headset);
        this.getActionBar().hide();
        localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void findViewById() {
        tv_headset = (TextView) findViewById(R.id.tv_headset);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);
        this.setTitle(mModuleName);
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
                Log.d(TAG, "Headset test is pass");
            }
        });
        builder.setNegativeButton(paramString2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
                Log.d(TAG, "Headset test is failed");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示未插耳机对话框
     */
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("No headset found");
        builder.setMessage("Please plug in the headset");
        //使dialog一直获取焦点
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //huangzhijian@wind-mobi.com modify 20161018 start
                startTest();
//                mFailCount++;
//                setTestResult(Constants.TEST_FAILED, mFailCount);
//                finishActivity(Constants.TEST_FAILED);
//                Log.d(TAG, "Headset test is failed");
                //huangzhijian@wind-mobi.com modify 20161018 end
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
            Log.d(TAG, "Headset Test finished ,send broadcast to run the next test module");
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
            if (module.getEnName().equals("Headset Test")) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
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
    protected void onPause() {
        super.onPause();
        //zhangtao@wind-mobi.com 2016/8/1 add begain
        flag = true;
        //zhangtao@wind-mobi.com 2016/8/1 add end
    }

    @Override
    protected void onResume() {
        super.onResume();
        //把底下的button移除
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        startTest();
    }

    @Override
    public void startTest() {
        //zhangtao@wind-mobi.com 2016/8/1 add begain
        flag = false;
        //zhangtao@wind-mobi.com 2016/8/1 add end
        if (localAudioManager.isWiredHeadsetOn()) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.check_spk_l_r);
            tv_headset.setVisibility(View.VISIBLE);
            mp.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mp.isPlaying()) {
                    }
                    handler.sendEmptyMessage(0);
                }
            }).start();

        } else {
            showErrorDialog();
        }
    }
}
//liaozhenbin@wind-mobi.com 20160112 mod end
