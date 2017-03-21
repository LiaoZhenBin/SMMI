package com.asus.atd.smmitest.manager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.receiver.ModuleTestReceiver;
import com.asus.atd.smmitest.testcase.CommonTestActivity;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.R;

/**
 * Created by liaozhenbin on 2015/12/25.
 */
public class MainManager {
    private final String TAG = this.getClass().getSimpleName();
    private final static boolean DEBUG = true;
    private Context context;
    private static MainManager mMainManager;
    private static ModuleManager mModuleManager;
    private BroadcastReceiver mModuleTestReceiver;

    public static boolean mIsAutoTest = false;
    public static boolean mIsSaveTestResultsToFile = true;
    public static ArrayList<TestCase> mTestCases;
    private static int RunningIndex = -1;
    public static final String modulePackageName = "com.asus.atd.smmitest";


    public MainManager(Context context) {
        this.context = context;
        mModuleTestReceiver = new ModuleTestReceiver();
        mModuleManager = ModuleManager.getInstance(context);
        mTestCases = new ArrayList<TestCase>();
    }

    public static MainManager getInstance(Context context) {
        if (mMainManager == null) {
            synchronized (MainManager.class) {
                if (mMainManager == null) {
                    mMainManager = new MainManager(context);
                }
            }
        }
        return mMainManager;
    }


    /**
     * 注册所有的广播
     */
    public void registerAll() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_MODULE_TEST);
        context.registerReceiver(mModuleTestReceiver, filter);
        if (DEBUG) Log.d(TAG, "mModuleTestReceiver has register");
    }

    /**
     * 解除注册的广播
     */
    public void unRegisterAll() {
        context.unregisterReceiver(mModuleTestReceiver);
        if (DEBUG) Log.d(TAG, "mModuleTestReceiver has unregister");
    }

    /**
     * 运行下一个测试模块
     */
    public void runNextTest() {

        if (++RunningIndex >= mTestCases.size()) {
            onAllFinish();
            Log.d(TAG, "All test module finish");
            return;
        }

        Log.d(TAG, "RunningIndex :" + RunningIndex);
        runNextModule(RunningIndex);
    }

    /**
     * 获取下一个运行模块
     *
     * @param position
     * @return method
     */
    public static void getNextModule(int position) {
        String className = modulePackageName + mTestCases.get(position).getActivity();
        HashMap map = mModuleManager.getMethod(className);
        ArrayList<Object> objList = (ArrayList) map.get("object");
        ArrayList<Method> methodList = (ArrayList) map.get("method");
        Object obj = objList.get(0);
        Method method = methodList.get(0);
        try {
            method.invoke(obj, new Class[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // return mModuleManager.getMethod(className);
        //return method;
    }

    public void runNextModule(int position) {
     /*   String className =modulePackageName + ".testcase." +mTestCases.get(position).getActivity()+"ing";
        Intent intent = new Intent().setComponent(new ComponentName(modulePackageName, className));
        intent.putExtra("CurrentIndex",position);
        if(mTestCases.get(position).getEnName().equals(context.getString(R.string.text_test_main_camera))){
            intent.putExtra("CameraType",Constants.MAIN_CAMERA_TEST);
        }else if(mTestCases.get(position).getEnName().equals(context.getString(R.string.text_test_front_camera))) {
            intent.putExtra("CameraType",Constants.FRONT_CAMERA_TEST);
        }
        context.startActivity(intent);*/

        //TODO 自动测试模式下添加Indicator提示当前测试进度
        if (!mModuleManager.mIsLoadModuleImage) {
            mModuleManager.loadModuleResource(mTestCases);
        }
        TestCase module = mTestCases.get(position);
        Intent intent = new Intent(context, CommonTestActivity.class);
        intent.putExtra("ModuleImage", mModuleManager.mTestModuleImage.get(module.getEnName()));
        intent.putExtra("ModuleName", module.getEnName());
        intent.putExtra("ModuleActivity", module.getActivity());
        intent.putExtra("CurrentIndex", position);
        if (mTestCases.get(position).getEnName().equals(context.getString(R.string.text_test_main_camera))) {
            intent.putExtra("CameraType", Constants.MAIN_CAMERA_TEST);
        } else if (mTestCases.get(position).getEnName().equals(context.getString(R.string.text_test_front_camera))) {
            intent.putExtra("CameraType", Constants.FRONT_CAMERA_TEST);
        }
        context.startActivity(intent);
    }

    /**
     * 开始自动测试
     */
    public void startAllTest() {
        registerAll();
        if (!mModuleManager.mXmlParsed) {
            if (DEBUG) Log.d(TAG, "the mXmlParse is false , start to parse the Xml");
            mModuleManager.parseXML();
        }

        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();
        if (DEBUG) Log.d(TAG, "all the test module have load ");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                runNextTest();
                if (DEBUG) Log.d(TAG, "run the first test module");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }
        }.execute();
    }

    /**
     * 结束所有的模块测试
     */
    public void finishAllTest() {
        unRegisterAll();
    }

    /**
     * 所有的模块测试完成
     * 保存测试数据到文件
     */
    public void onAllFinish() {
        saveResultToFile();
        clear();
        unRegisterAll();
    }

    /**
     * 保存测试结果
     */
    public void saveResultToFile() {
        if (mMainManager.mIsAutoTest && mMainManager.mIsSaveTestResultsToFile) {
            Intent intent = new Intent(Constants.ACTION_EMODE_UPDATE_RESULT_FILE);
            intent.setPackage(context.getPackageName());
            context.startService(intent);
        }
    }

    /**
     * 清理测试模块
     */
    public void clear() {
        Log.d(TAG, "start to clear ... ");
        RunningIndex = -1;
    }

    /**
     * 测试单元运行结束时调用
     */
    public void sendFinishBroadcast(final int result, final String moduleName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_TEST_FINISH, Constants.TEST_FINISH);
                intent.putExtra("result", result);
                intent.putExtra("code", moduleName);
                intent.setAction(Constants.BROADCAST_MODULE_TEST);
                context.sendBroadcast(intent);
                if (DEBUG) Log.d(TAG, "send a broadcast to run the next test module");
            }
        }).start();
    }

    /**
     * 测试单元运行结束时的回调
     */
    private OnTestFinishedCallback callback;

    public interface OnTestFinishedCallback {
        public void onSaveTestResult();
    }

    /**
     * 设置回调
     *
     * @param callBack
     */
    public void setOnTestFinishedCallback(OnTestFinishedCallback callBack) {
        this.callback = callBack;
    }


}
