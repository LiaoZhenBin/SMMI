package com.asus.atd.smmitest.testcase;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//liaozhenbin@wind-mobi.com 20160702 mod begin
import android.view.MenuItem;
//liaozhenbin@wind-mobi.com 20160702 mod end

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.R;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class CommonTestActivity extends BaseActivity {
    private Button mBtnStart;
    private TextView tv_result;
    private ImageView iv_result;
    private int mIdModuleImage;
    private String mModuleName;
    private String mModuleActivity;
    private MainManager mMainManager;
    private boolean isReceived = false;
    private LinearLayout layout_bottom;
    private TextView tv_preModule;
    private TextView tv_nextModule;
    private TextView tv_currentIndex;
    private String mPreModule;
    private String mNextModule;
    private String mCurProgress;
    private int mCurrentIndex;
    private String mModuleStr;
    private int mTestResult = -1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_common);
        Intent intent = getIntent();
        mIdModuleImage = intent.getIntExtra("ModuleImage", 0);
        mModuleName = intent.getStringExtra("ModuleName");
        mModuleActivity = intent.getStringExtra("ModuleActivity");
        mCurrentIndex = intent.getIntExtra("CurrentIndex", -1);
        // gaohui@wind-mobi.com 2016/03/17 add start
        mModuleStr = intent.getStringExtra("ModuleStr");
        Log.d(TAG, mModuleStr);
        // gaohui@wind-mobi.com 2016/03/17 add end
        setTitle(mModuleName);
    }

    @Override
    protected void findViewById() {
        ((ImageView) findViewById(R.id.iv_common)).setImageResource(mIdModuleImage);
        // gaohui@wind-mobi.com 2016/03/17 mod start
        Log.d(TAG, mModuleStr);
        ((TextView) findViewById(R.id.tv_common)).setText(mModuleStr);
//        ((TextView)findViewById(R.id.tv_common)).setText(mModuleName);
        // gaohui@wind-mobi.com 2016/03/17 mod end
        mBtnStart = (Button) this.getWindow().getDecorView().findViewById(R.id.btn_start);
        tv_result = (TextView) findViewById(R.id.tv_result_common);
        iv_result = (ImageView) findViewById(R.id.iv_result_common);
        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom_common);
        tv_preModule = (TextView) findViewById(R.id.tv_pre_module_common);
        tv_nextModule = (TextView) findViewById(R.id.tv_next_module_common);
        tv_currentIndex = (TextView) findViewById(R.id.tv_indication_common);
    }


    /**
     * 清理状态
     */
    public void clearStatus() {
        if (tv_result.getVisibility() == View.VISIBLE) tv_result.setVisibility(View.GONE);
        if (iv_result.getVisibility() == View.VISIBLE) iv_result.setVisibility(View.GONE);
    }

    /**
     * 设置状态
     *
     * @param result
     */
    public void setStatus(int result) {
        tv_result.setVisibility(View.VISIBLE);
        iv_result.setVisibility(View.VISIBLE);
        switch (result) {
            case Constants.TEST_PASS:
                Log.d(TAG, "setStatus pass");
                iv_result.setImageResource(R.drawable.asus_diagnostic_ic_pass);
                tv_result.setText(mModuleActivity + " " + getString(R.string.text_pass_result));
                tv_result.setTextColor(Color.GREEN);
                mBtnStart.setText(getString(R.string.btn_test_again));
                break;
            case Constants.TEST_FAILED:
                Log.d(TAG, "setStatus failed");
                iv_result.setImageResource(R.drawable.asus_diagnostic_ic_fail);
                tv_result.setText(mModuleActivity + " " + getString(R.string.text_fail_result));
                tv_result.setTextColor(Color.RED);
                mBtnStart.setText(getString(R.string.btn_test_again));
                break;
        }

    }

    public void setBtnText() {
        tv_result.setVisibility(View.VISIBLE);
        tv_result.setText(getString(R.string.text_testing));
        tv_result.setTextColor(Color.RED);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mMainManager = MainManager.getInstance(this);
        showTestIndicator();
        isReceived = false;
        register();
    }

    /**
     * 自动测试显示previous、current、next测试模块
     */
    public void showTestIndicator() {
        if (!mMainManager.mIsAutoTest) {
            layout_bottom.setVisibility(View.GONE);
            return;
        }
        layout_bottom.setVisibility(View.VISIBLE);
        mPreModule = (mCurrentIndex == 0) ? "" : mMainManager.mTestCases.get(mCurrentIndex - 1).getCnName();
        mNextModule = (mCurrentIndex == mMainManager.mTestCases.size() - 1) ? "" : mMainManager.mTestCases.get(mCurrentIndex + 1).getCnName();
        mCurProgress = (mCurrentIndex + 1) + "/" + mMainManager.mTestCases.size();
        tv_preModule.setText(mPreModule);
        tv_nextModule.setText(mNextModule);
        tv_currentIndex.setText(mCurProgress);
    }


    /**
     * 开始测试
     */
    @Override
    public void startTest() {
        clearStatus();
        setBtnText();
        String className = mMainManager.modulePackageName + ".testcase." + mModuleActivity + "ing";
        Intent intent = new Intent().setComponent(new ComponentName(mMainManager.modulePackageName, className));
        if (mModuleName.equals(getString(R.string.text_test_main_camera))) {
            intent.putExtra("CameraType", Constants.MAIN_CAMERA_TEST);
        } else if (mModuleName.equals(getString(R.string.text_test_front_camera))) {
            intent.putExtra("CameraType", Constants.FRONT_CAMERA_TEST);
        }
        if (mMainManager.mIsAutoTest) {
            startActivity(intent);
            finish();
            Log.d(TAG, "start to test module:" + mModuleActivity + " then finish this");
        } else {
            startActivityForResult(intent, 0);
        }
        Log.d(TAG, "start to test module:" + mModuleActivity);
    }

    /**
     * 接收测试返回结果进行相应处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // liaozhenbin@wind-mobi.com modify # 2017/1/20 begin
        if (!isReceived) {
            setStatus(resultCode);
            mTestResult = resultCode;
        }
        // liaozhenbin@wind-mobi.com modify # 2017/1/20 end
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reStartTest() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    private BroadcastReceiver mBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constants.ACTION_MODULT_TEST_RETURN) {
                int resultCode = intent.getIntExtra("ResultCode", 1);
                isReceived = true;
                // liaozhenbin@wind-mobi.com modify # 2017/1/20 begin
                mTestResult = resultCode;
                // liaozhenbin@wind-mobi.com modify # 2017/1/20 end
                setStatus(resultCode);
                Log.d(TAG, "receiver a resultCode from test module:" + resultCode);
            }
        }
    };

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_MODULT_TEST_RETURN);
        registerReceiver(mBroadReceiver, filter);
    }

    public void unRegister() {
        unregisterReceiver(mBroadReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMainManager.mIsAutoTest && KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("ModuleName", mModuleName);
        setResult(mTestResult, intent);
        Log.d("lizusheng", "onBackPressed");
        super.onBackPressed();
    }

    /* @Override
     public void onAttachedToWindow() {
         this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
         super.onAttachedToWindow();
     }*/
    //    liaozhenbin@wind-mobi.com  2016/6/30 add
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("ModuleName", mModuleName);
                setResult(mTestResult, intent);
                finishActivity();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //     liaozhenbin@wind-mobi.com  2016/6/30 end
}
