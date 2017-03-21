package com.asus.atd.smmitest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;

/**
 * Created by liaozhenbin on 2015/12/28.
 */

/**
 * 接收测试单元测试结束时的广播
 * 执行下一个单元测试
 */
public class ModuleTestReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    Context context;
    private int result;
    private String code;
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "start to save the result");
            SaveTestResult.getInstance(context).saveResult(result, code);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        int isFinish = intent.getIntExtra(Constants.KEY_TEST_FINISH, -1);
        if (isFinish == Constants.TEST_FINISH) {
            Log.d(TAG, "received a finish broadcast, save the result and run the next test");
            result = intent.getIntExtra("result", 0);
            code = intent.getStringExtra("code");
            Log.d(TAG, "TestResult:" + result + "  module:" + code);
            if (code != null) {
                mHandler.post(mRunnable);
            }
            MainManager.getInstance(context).runNextTest();
        }
    }
}

