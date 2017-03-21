package com.asus.atd.smmitest.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.asus.atd.smmitest.R;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.ModuleManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lizusheng on 2016/6/16.
 */
public class UploadUtils {

    private static final String TAG = UploadUtils.class.getSimpleName();
    /**
     * 检测当前网络状态
     */
    public static boolean isNetworkConnected(Context paramContext)
    {
        return ((ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    /**
     * 检查是否有测试结果
     * @param context
     * @return
     */
    public static boolean checkHasTest(Context context){
        String testResults = SPUtils.get(context, Constants.PREF_KEY_TEST_RESULTS_PHONE, "", Constants.PREF_NAME_TEST_RESULTS);
        Log.d(TAG, "testResults : "+testResults);
        return !testResults.isEmpty() ;
    }


    /**
     * 获取ProductType
     * @param paramString
     * @return
     */
    public static String getProductType(String paramString)
    {
        String str = "OK";
        if ((paramString.length() > 0) && (paramString.substring(2, 4).equals("AT"))) {
            str = "AT";
        }
        return str;
    }


    /**
     * 获取SSN
     * @return
     */
    public static String getSSN()
    {
        return getSystemOutput("getprop ro.serialno").replace("\n", "");
    }

    /**
     * 获取ISN
     * @return
     */
    public static String getISN()
    {
        return getSystemOutput("getprop ro.isn").replace("\n", "");
    }


    /**
     * 获取ToolVersion
     */
    public static String getToolVersion(Context context){
        return context.getString(R.string.version_smmitest_tools);
    }

    /**
     *获取系统版本号
     */
    public static String getOS_Version()
    {
        String str = getSystemOutput("getprop ro.build.csc.version").replace("\n", "");
        if (str.equals("")) {
            return "NULL";
        }
        return str;
    }

    /**
     * 获取测试时间
     * @return
     */
    public static String[] getTestDateTime(){
        String[] res = new String[2];
        Date localDate = new Date();
        SimpleDateFormat localSimpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        res[0] = localSimpleDateFormat1.format(localDate);
        SimpleDateFormat localSimpleDateFormat2 = new SimpleDateFormat("kk:mm:ss");
        res[1] = localSimpleDateFormat2.format(localDate);
        return res;
    }


    public static String getSystemOutput(String paramString)
    {
        String str1 = "";
        try
        {
            Process localProcess = Runtime.getRuntime().exec(paramString);
            InputStream localInputStream = localProcess.getInputStream();
            InputStreamReader localInputStreamReader = new InputStreamReader(localInputStream);
            BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
            for (;;)
            {
                String str2 = localBufferedReader.readLine();
                if (str2 == null) {
                    break;
                }
                StringBuilder localStringBuilder1 = new StringBuilder();
                str1 = str1 + str2;
                StringBuilder localStringBuilder2 = new StringBuilder();
                str1 = str1 + "\n";
            }
            int i = localProcess.waitFor();
            PrintStream localPrintStream = System.out;
            StringBuilder localStringBuilder3 = new StringBuilder();
            localPrintStream.println("Process exitValue: " + i);
            return str1;
        }
        catch (Throwable localThrowable)
        {
            for (;;)
            {
                localThrowable.printStackTrace();
            }
        }
    }

    /**
     * 获取并处理测试结果
     * @param context
     * @return
     */
    public static List getResultForUpload(Context context){
        List<String> resList = new ArrayList<String>();
        String str_result = "PASS";
        String str_errorcode = "";
        String str_passitem = "";
        String testResults = SPUtils.get(context, Constants.PREF_KEY_TEST_RESULTS_PHONE, "", Constants.PREF_NAME_TEST_RESULTS);
        ArrayList<TestCase> mTestCases =LoadTestModule(context);
        Log.d(TAG,"getResultForUpload:"+testResults);

        if (!testResults.isEmpty()){
            String[] testModules = testResults.split(",");
            for (int j=0;j<testModules.length;j++) {
                String[] testModult = testModules[j].split("=");
                if (Integer.parseInt(testModult[1])==1) {
                    for (TestCase module:mTestCases){
                        if (module.getEnName().equals(testModult[0])) {
                            if (str_errorcode.equals("")) {
                                str_errorcode = String.valueOf(module.getErrorCode());
                            }else {
                                str_errorcode = str_errorcode +","+String.valueOf(module.getErrorCode());
                            }
                        }
                    }

                    Log.d(TAG,"str_errorcode:"+str_errorcode);
                }else {
                    if (str_passitem.equals("")) {
                        str_passitem = testModult[0].split(" ")[0];
                    }else {
                        str_passitem = str_passitem +","+testModult[0].split(" ")[0];
                    }
                    Log.d(TAG,"str_passitem:"+str_passitem);
                }
            }

            if (!str_errorcode.equals("")){
                str_result = "FAIL";
            }

            resList.add(str_result);
            resList.add(str_errorcode);
            resList.add(str_passitem);
        }

        return resList;
    }


    /**
     * 加载测试项
     */
    public static ArrayList<TestCase> LoadTestModule(Context context){
        ModuleManager mModuleManager= ModuleManager.getInstance(context);
        ArrayList<TestCase> mTestCases =new ArrayList<TestCase>();
        if (!mModuleManager.mXmlParsed) {
            mModuleManager.parseXML();
        }
        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();

        return mTestCases;
    }
}
