package com.asus.atd.smmitest.testcase;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
 * Created by huangzhijian on 2016/1/14.
 */
public class
        HeadsetKeyTesting extends BaseActivity {
    private TextView headsetkay;
    private Button headSetKeyButton;
    private static final String mModuleName = "Headset Key Test";
    private ModuleManager mModuleManager ;
    public static int mSucCount = 0;
    public static int mFailCount = 0;

    /**
     * 加载布局文件
     */
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_headsetkey);
    }

/**
 * 获取view的ID
 */
    @Override
    protected void findViewById() {
        headsetkay = (TextView) findViewById(R.id.headset_key_text);
        headSetKeyButton=(Button)findViewById(R.id.headset_key_button);
        setTitle(mModuleName);
    }

    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        //huangzhijian@wind-mobi.com modify 20161018 start
//        AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if(!localAudioManager.isWiredHeadsetOn()){
//            //liaozhenbin@wind-mobi.com 20160721 add begin
//            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//            //liaozhenbin@wind-mobi.com 20160721 add end
//            builder.setTitle(R.string.text_headsetkey_dialogtitle);    //标题
//            builder.setCancelable(false);   //不响应back按钮
//            builder.setMessage(R.string.text_headsetkey_dialogtext); //对话框显示内容
//            //设置按钮
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mFailCount++;
//                    setTestResult(Constants.TEST_FAILED,mFailCount);
//                    finishActivity(Constants.TEST_FAILED);
//                    setResult(Constants.TEST_FAILED);
//                }});
//            builder.create().show();
//        }
        startTest();
        //huangzhijian@wind-mobi.com modify 20161018 end
    }


    /**
     * 去掉start 的button
     */
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }



    /**
     * 测试正常结束返回
     * 根据是单项测试/自动测试：
     * 自动测试 --- MainManager.getInstance().sendFinishBroadcast()
     * 单项测试 --- 更新测试结果
     * @param resultCode
     */
    public void finishActivity(int resultCode){
        if (MainManager.getInstance(this).mIsAutoTest){
            MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
            finish();
            Log.d(TAG,"BluetoothTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
            setResult(resultCode);
            finish();
        }
    }


    public void setTestResult(int code,int count){
        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("Headset Key Test")){
                if(code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }


    /**
     * 手动结束测试返回
     */
    @Override
    public void onBackPressed() {
        mFailCount++;
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED ,mModuleName);
        setTestResult(Constants.TEST_FAILED,mFailCount);
        setResult(Constants.TEST_FAILED);
        super.onBackPressed();
    }

    /**
     * 监听耳机按键
     * @param keyCode 按键的code
     * @param event   按键事件
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {
            mSucCount++;
            setTestResult(Constants.TEST_PASS, mSucCount);
            finishActivity(Constants.TEST_PASS);
            setResult(Constants.TEST_PASS);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void setListener() {
        headSetKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED,mFailCount);
                finishActivity(Constants.TEST_FAILED);
                setResult(Constants.TEST_FAILED);
            }
        });
    }

    @Override
    protected void init() {

    }
}
