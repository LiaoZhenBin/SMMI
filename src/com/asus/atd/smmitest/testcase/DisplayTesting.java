package com.asus.atd.smmitest.testcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
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
 * Created by liaozhenbin on 2016/1/11.
 */
//liaozhenbin@wind-mobi.com 20160111 mod begin
//实现Display Test模块
public class DisplayTesting extends BaseActivity {
    private ImageView iv_display;
    private int count = 0;
    private static final String mModuleName = "Display Test";
    private ModuleManager mModuleManager ;
    private static int mSucCount = 0;
    private static int mFailCount = 0;


    private static final int [] colors = {R.color.red, R.color.green, R.color.blue, R.color.black,
                                            R.color.white, R.color.gray, R.color.black1, R.color.silver};

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_display);
        this.getActionBar().hide();
    }

    @Override
    protected void findViewById() {
        iv_display = (ImageView) findViewById(R.id.iv_display);
    }

    /**
     * 点击
     */
    @Override
    protected void setListener() {
        iv_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < 7) {
                    iv_display.setBackgroundResource(colors[count]);
                    count++;
                } else if (count == 7) {
//                    Toast.makeText(DisplayTesting.this,"测试完成！",Toast.LENGTH_SHORT).show();
                    showPassFailDialog("PASS","FAIL");
                }
            }
        });
    }

    /**
     * 显示成功失败选择对话框
     * @param paramString1
     * @param paramString2
     */
    private void showPassFailDialog(String paramString1, String paramString2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
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
                Log.d(TAG, "Display test is pass");
            }
        });
        builder.setNegativeButton(paramString2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
                Log.d(TAG, "Display test is failed");
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
     * @param resultCode
     */
    public void finishActivity(int resultCode){
        if (MainManager.getInstance(this).mIsAutoTest){
            MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
            finish();
            Log.d(TAG, "DisplayTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
            setResult(resultCode);
            finish();
        }
    }

    /**
     * 设置测试返回信息
     * @param code
     * @param count
     */
    public void setTestResult(int code,int count){
        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("Display Test")){
                if(code == Constants.TEST_PASS)
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
     * @param paramInt
     * @param paramKeyEvent
     * @return
     */
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //把底下的button移除
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        Toast.makeText(this,getString(R.string.toast_display),Toast.LENGTH_SHORT).show();
    }
}
//liaozhenbin@wind-mobi.com 20160111 mod begin
