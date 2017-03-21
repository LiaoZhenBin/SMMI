package com.asus.atd.smmitest.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.ModuleManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lizusheng on 2015/12/28.
 */
public class FileUtils {
    public static final String TAG = "FileUtils";
    public static final String NOT_EXISTS = "NOT_EXISTS";
    public static final String IO_ERROR = "IO_ERROR";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void byte2image(byte[] data, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean prepareFile(String path) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        File destDir = new File(path).getParentFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
            //  此处不可使用打印到本地 会造成回环
        }
        return true;
    }

    public static String getText(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return NOT_EXISTS;
        }

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return IO_ERROR;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除测试数据
     */
    public static void deleteTestResult() {
        File file = new File(Environment.getExternalStorageDirectory(), Constants.PREF_KEY_TEST_RESULT_FILE_NAME);
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "The testResult has deleted !");
        }
    }

    /**
     * 将测试数据保存到文件
     *
     * @param sBuilder
     */
    public static void saveStringToFile(StringBuilder sBuilder) {
        long startTime = System.currentTimeMillis();
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), Constants.PREF_KEY_TEST_RESULT_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(sBuilder.toString().getBytes());
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "updateTestResultToFile", e);
        } catch (IOException e) {
            Log.e(TAG, "updateTestResultToFile", e);
        } finally {
            try {
                bos.close();
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, "updateTestResultToFile", e);
            }
        }
        Log.v(TAG, "update result file takes " + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * 更新测试数据文件
     *
     * @param context
     */
    public static void updateTestResultFile(Context context) {
        ModuleManager mModuleManager = ModuleManager.getInstance(context);
        String testResults = SPUtils.get(context, Constants.PREF_KEY_TEST_RESULTS_PHONE, "", Constants.PREF_NAME_TEST_RESULTS);
        Log.d(TAG, "testResults : " + testResults);
        String[] testResultsPairs = testResults.split(",");
        String[] testResultsItem;
        int[] testResultsValues = new int[mModuleManager.mTestModule.size()];

        final int size = mModuleManager.mTestModule.size();
        final int len = testResultsPairs.length;
        for (int i = 0; i < size; i++) {
            boolean isTested = false;
            for (int j = 0; j < len; j++) {
                testResultsItem = testResultsPairs[j].split("=");
                if (testResultsItem[0].equals(mModuleManager.mTestModule.get(i).getEnName())) {
                    isTested = true;
                    int resultItem = Integer.parseInt(testResultsItem[1]);
                    Log.d(TAG, "resultItem:" + testResultsItem[0]);
                    switch (resultItem) {
                        case Constants.TEST_PASS:
                            testResultsValues[i] = resultItem;
                            Log.d(TAG, testResultsItem[0] + " test pass");
                            break;
                        case Constants.TEST_FAILED:
                            testResultsValues[i] = mModuleManager.mTestModule.get(i).getErrorCode();
                            Log.d(TAG, testResultsItem[0] + " test failed");
                            break;
                        default:
                            testResultsValues[i] = Constants.NOT_TEST;
                            Log.d(TAG, testResultsItem[0] + " has not test");
                            break;
                    }
                }
            }
            if (!isTested) {
                testResultsValues[i] = -1;
            }
        }

        StringBuilder fileSB = new StringBuilder();
        int testNum = 0;
        fileSB.append("[SMMI Test Result]\n");
        for (TestCase tc : mModuleManager.mTestModule) {
            fileSB.append(tc.getEnName());
            fileSB.append(",");
            fileSB.append(tc.getErrorCode());
            fileSB.append(",Initialize\n");
        }
        for (int i = 0; i < size; i++) {
            if (testResultsValues[i] >= 0) {
                fileSB.append(mModuleManager.mTestModule.get(i).getEnName());
                fileSB.append(",");
                fileSB.append(testResultsValues[i]);
                if (testResultsValues[i] == Constants.TEST_PASS) {
                    fileSB.append(",PASS\n");
                    testNum++;
                    Log.d(TAG, mModuleManager.loadTestModule().get(i).getEnName() + " pass");
                } else if (testResultsValues[i] == Constants.NOT_TEST) {
                    testNum++;
                    fileSB.append(",Initialize\n");
                } else {
                    testNum++;
                    fileSB.append(",FAIL\n");
                }
            }
        }
        if (testNum == size) {
            fileSB.append("[SMMI All Test Done]\n");
        }
        saveStringToFile(fileSB);
    }

    /**
     * 初始化测试数据文件
     *
     * @param context
     */
    public static void initTestResultFile(Context context) {
        ModuleManager mModuleManager = ModuleManager.getInstance(context);
        if (mModuleManager.mXmlParsed) {
            StringBuilder fileSB = new StringBuilder();
            StringBuilder prefSB = new StringBuilder();
            fileSB.append("[SMMI Test Result]\n");
            for (TestCase tc : mModuleManager.mTestModule) {
                fileSB.append(tc.getEnName());
                fileSB.append(",");
                fileSB.append(tc.getErrorCode());
                fileSB.append(",Initialize\n");
    /*          prefSB.append(tc.getEnName().replaceAll("\\s+", "_"));
                prefSB.append("=");
                prefSB.append(Constants.NOT_TEST);
                prefSB.append(",");*/
            }
          /*  prefSB.deleteCharAt(prefSB.length() - 1);
            SPUtils.put(context, Constants.PREF_KEY_TEST_RESULTS_PHONE, prefSB.toString(), Constants.PREF_NAME_TEST_RESULTS);*/
            saveStringToFile(fileSB);
            SPUtils.put(context, Constants.PREF_KEY_TEST_RESULT_FILE_INITIALIZED, true);
        }
    }


}
