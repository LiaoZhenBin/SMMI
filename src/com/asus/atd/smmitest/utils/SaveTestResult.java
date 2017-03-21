package com.asus.atd.smmitest.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class SaveTestResult {
    private final String TAG = this.getClass().getSimpleName();
    public static SaveTestResult mSaveTestResult;
    Context context;
    ModuleManager mModuleManager;
    MainManager mMainManager;

    public SaveTestResult(Context context) {
        this.context = context;
        mModuleManager = ModuleManager.getInstance(context);
        mMainManager = MainManager.getInstance(context);
    }

    public static SaveTestResult getInstance(Context context) {
        if (mSaveTestResult == null) {
            synchronized (SaveTestResult.class) {
                if (mSaveTestResult == null) {
                    mSaveTestResult = new SaveTestResult(context);
                }
            }
        }
        return mSaveTestResult;
    }

    /**
     * 保存测试结果到SP
     *
     * @param result
     * @param Code
     */
    public void saveResult(int result, String Code) {
        Log.d(TAG, "result:" + result, "Code:" + Code);

        if (mModuleManager.mIsCustomizeErrorCode && Constants.TEST_FAILED == result) {
            result = mModuleManager.getErrorCode(Code);
        }
        if (mModuleManager.mTestcaseType == Constants.TESTCASE_TYPE_OTHER) {
            return;
        }

        String oldResults = SPUtils.get(context, Constants.PREF_KEY_TEST_RESULTS_PHONE, "", Constants.PREF_NAME_TEST_RESULTS);
        String newResults = setValue(Code, oldResults, result);
        SPUtils.put(context, Constants.PREF_KEY_TEST_RESULTS_PHONE, newResults, Constants.PREF_NAME_TEST_RESULTS);

        //  checkMMIResult(newResults, len);

        if (!mMainManager.mIsAutoTest && mMainManager.mIsSaveTestResultsToFile) {
            Intent intent = new Intent(Constants.ACTION_EMODE_UPDATE_RESULT_FILE);
            intent.setPackage(context.getPackageName());
            context.startService(intent);
        }
    }

    /**
     * 更新测试结果到SP
     *
     * @param name
     * @param str
     * @param value
     * @return
     */
    private String setValue(String name, String str, int value) {
        Log.d(TAG, "setValue, old results str: " + str);

        String newStr = "";

        if (TextUtils.isEmpty(str)) {
            newStr = name + "=" + value;
        } else if (!str.contains(",")) {
            String[] tp = str.split("=");

            if (tp[0].equals(name)) {
                newStr = name + "=" + value;
            } else {
                newStr = str + "," + name + "=" + value;
            }
        } else {
            String key = name + "=";

            if (str.startsWith(key)) {
                newStr = key + value + str.substring(str.indexOf(","));
            } else {
                int startIndex = str.indexOf(key);

                if (startIndex == -1) {
                    newStr = str + "," + key + value;
                } else {
                    int endIndex = str.indexOf(",", startIndex);
                    if (endIndex == -1) {
                        newStr = str.substring(0, startIndex) + key + value;
                    } else {
                        newStr = str.substring(0, startIndex) + key + value +
                                str.substring(endIndex);
                    }
                }
            }
        }

        Log.d(TAG, "setValue, new results str: " + newStr);
        return newStr;
    }


    private void checkMMIResult(String results, int len) {
        String[] pairs = results.split(",");

        if (pairs.length < len) {
        } else {
            int size = pairs.length;
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(pairs[i].split("=")[1]) != Constants.TEST_PASS) {
                    setAutoMMIResult(false);
                    return;
                }
            }
            setAutoMMIResult(true);
        }
    }

    private void setAutoMMIResult(boolean val) {
   /*     byte[] buff = NvRAMAgentUtils.readFile();//new byte[64];
        *//*
        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            int count = is.read(buff);
            is.close();

            if (count == 64) {
                int index = (EmodeApp.mTestCaseType == TestCaseType.BOARD) ? 59 : 58;
                buff[index] = (val ? "Y" : "N").getBytes()[0];

                FileOutputStream os = new FileOutputStream("/dev/pro_info");
                os.write(buff);
                os.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*//*
        if(buff != null){
            int index = (ModuleManager.getInstance(context).mTestcaseType == Constants.TESTCASE_TYPE_BOARD) ? 59 : 58;
            buff[index] = (val?"Y":"N").getBytes()[0];
            NvRAMAgentUtils.writeFile(buff);
        }*/
    }


}
