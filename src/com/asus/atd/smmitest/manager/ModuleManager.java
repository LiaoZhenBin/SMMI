package com.asus.atd.smmitest.manager;

import android.content.Context;

import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.XmlParse;
import com.asus.atd.smmitest.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liaozhenbin on 2015/12/29.
 */
public class ModuleManager {

    private final String TAG = this.getClass().getSimpleName();
    private static ModuleManager mModuleManager;
    private Context context;
    public static HashMap<String, TestCase> mTestcases = new HashMap<String, TestCase>();
    // gaohui@wind-mobi.com 2016/03/17 add start
    public static HashMap<String, Integer> mTestModuleStr = new HashMap<String, Integer>();
    // gaohui@wind-mobi.com 2016/03/17 add end
    public static HashMap<String, Integer> mTestModuleImage = new HashMap<String, Integer>();
    public static ArrayList<TestCase> mTestModule = new ArrayList<TestCase>();
    public static ArrayList<TestCase> mPhoneTestcases = new ArrayList<TestCase>();
    public static ArrayList<TestCase> mBoardTestcases = new ArrayList<TestCase>();
    public static ArrayList<TestCase> mOtherTestcases = new ArrayList<TestCase>();
    public static int mTestcaseType = Constants.TESTCASE_TYPE_DEFAULT;
    public static boolean mXmlParsed = false;
    public static boolean mIsCustomizeErrorCode = false;
    public static boolean mIsLoadModuleImage = false;


    public ModuleManager(Context context) {
        this.context=context;
    }

    public static ModuleManager getInstance(Context context) {
        if (mModuleManager == null) {
            synchronized (ModuleManager.class) {
                if (mModuleManager == null) {
                    mModuleManager = new ModuleManager(context);
                }
            }
        }
        return mModuleManager;
    }

    /**
     * 清理测试单元
     */
    public void clearTestCase(){
        mTestcases.clear();
        mPhoneTestcases.clear();
        mBoardTestcases.clear();
        mOtherTestcases.clear();
        Log.d(TAG,"all the testcases has clear");
    }

    /**
     * 解析测试项的XML配置文件
     */
    public void parseXML(){
        clearTestCase();
        XmlParse.parseXml(context);
    //    mXmlParsed = true;
        Log.d(TAG, "start to parse XML");
    }

    /**
     * 加载测试单元
     */
    public List<TestCase> loadTestModule(){
        Log.d(TAG, "start to load test module ...");
        switch (mTestcaseType){
            case Constants.TESTCASE_TYPE_PHONE:
                return mPhoneTestcases;
            case Constants.TESTCASE_TYPE_BOARD:
                return mBoardTestcases;
            case Constants.TESTCASE_TYPE_OTHER:
                return mOtherTestcases;
            default:
                return mTestModule;
        }
    }


    /**
     * 加载测试项资源
     * @param mTestCases
     * @return
     */
    public List loadModuleResource(ArrayList<TestCase> mTestCases){
        ArrayList <HashMap<String,Integer>> mApapterList = new ArrayList<HashMap<String,Integer>>();
        for (TestCase module:mTestCases){
            HashMap<String,Integer> mAdapterMap=new HashMap<String, Integer>();
            switch (module.getEnName()){
                case "Bluetooth Test":
                    mAdapterMap.put("Name", R.string.text_test_bluetooth);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_bt_test);
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_bt_test);
                    // gaohui@wind-mobi.com 2016/04/07 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_bluetooth);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    Log.d(TAG, "add Bluetooth to map");
                    break;
                case "WiFi Test":
                    mAdapterMap.put("Name", R.string.text_test_wifi);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_wifibus_test);
                    // gaohui@wind-mobi.com 2016/04/07 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_wifi);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_wifibus_test);
                    break;
                case "Battery Test":
                    mAdapterMap.put("Name", R.string.text_test_battery);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_battery_test);
                    // gaohui@wind-mobi.com 2016/04/07 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_battery);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_battery_test);
                    break;
                case "ECompass Test":
                    mAdapterMap.put("Name", R.string.text_test_ecompass);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_ecompass_test);
                    // gaohui@wind-mobi.com 2016/04/07 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_ecompass);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_ecompass_test);
                    break;
//                heliguang@wind-mobi.com 20160221 modify from ModemBus Test to Modem Test begin
                case "Modem Test":
//                heliguang@wind-mobi.com 20160221 modify from ModemBus Test to Modem Test end
                    mAdapterMap.put("Name", R.string.text_test_modem);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_modembus_test);
                    // gaohui@wind-mobi.com 2016/04/07 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_modem);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_modembus_test);
                    break;
                case "GSensor Test":
                    mAdapterMap.put("Name", R.string.text_test_gsensor);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_gsensor_test);
                    // gaohui@wind-mobi.com 2016/03/17 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_gsensor);
                    // gaohui@wind-mobi.com 2016/03/17 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_gsensor_test);
                    break;
                case "Keypad Test":
                    mAdapterMap.put("Name", R.string.text_test_keypad);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_keypad_test);
                    // gaohui@wind-mobi.com 2016/03/17 add end
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_keypad);
                    // gaohui@wind-mobi.com 2016/03/17 add start
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_keypad_test);
                    break;
                case "Touch Test":
                    mAdapterMap.put("Name", R.string.text_test_touch);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_touch_test);
                    // gaohui@wind-mobi.com 2016/03/17 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_touch);
                    // gaohui@wind-mobi.com 2016/03/17 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_touch_test);
                    break;
                case "Multitouch Test":
                    mAdapterMap.put("Name", R.string.text_test_multitouch);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_multitouch_test);
                    // gaohui@wind-mobi.com 2016/03/17 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_multitouch);
                    // gaohui@wind-mobi.com 2016/03/17 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_multitouch_test);
                    break;
                case "Vibrator Test":
                    mAdapterMap.put("Name", R.string.text_test_vibrator);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_vibrator_test);
                    // gaohui@wind-mobi.com 2016/03/17 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_vibrator);
                    // gaohui@wind-mobi.com 2016/03/17 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_vibrator_test);
                    break;
                case "Receiver Test":
                    mAdapterMap.put("Name", R.string.text_test_receiver);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_receiver_test);
                    //xiejiaming@wind-mobi.com 20160317 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.description_receiver_test);
                    //xiejiaming@wind-mobi.com 20160317 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_receiver_test);
                    break;
                case "Speaker Test":
                    mAdapterMap.put("Name", R.string.text_test_speaker);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_speaker_test);
                    //xiejiaming@wind-mobi.com 20160317 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.description_speaker_test);
                    //xiejiaming@wind-mobi.com 20160317 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_speaker_test);
                    break;
                case "BroadMic Test":
                    mAdapterMap.put("Name", R.string.text_test_broadmic);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_boardmic_test);
                    //xiejiaming@wind-mobi.com 20160317 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.desctiption_boardmic_test);
                    //xiejiaming@wind-mobi.com 20160317 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_boardmic_test);
                    break;
                case "Main Camera Capture Test":
                    mAdapterMap.put("Name", R.string.text_test_main_camera);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_maincamcapture_test);
                    //xiejiaming@wind-mobi.com 20160317 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.description_maincamera_test);
                    //xiejiaming@wind-mobi.com 20160317 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_maincamcapture_test);
                    break;
                case "VGACam Capture Test":
                    mAdapterMap.put("Name", R.string.text_test_front_camera);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_vgacamcapture_test);
                    //xiejiaming@wind-mobi.com 20160317 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.description_VGACam_test);
                    //xiejiaming@wind-mobi.com 20160317 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_vgacamcapture_test);
                    break;
                case "CameraFlash Test":
                    mAdapterMap.put("Name", R.string.text_test_camera_flash);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_cameraflash_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_cameraflash);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_cameraflash_test);
                    break;
                case "Display Test":
                    mAdapterMap.put("Name", R.string.text_test_display);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_display_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_display);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_display_test);
                    break;
                case "Headset Test":
                    mAdapterMap.put("Name", R.string.text_test_headset);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_headset_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_headset);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_headset_test);
                    break;
                case "Headset Mic Test":
                    mAdapterMap.put("Name", R.string.text_test_headset_mic);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_headsetmic_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_headsetmic);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_headsetmic_test);
                    break;
                case "Headset Key Test":
                    mAdapterMap.put("Name", R.string.text_test_headset_key);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_headsetkey_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_headsetkey);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_headsetkey_test);
                    break;
                case "SDcard Test":
                    mAdapterMap.put("Name", R.string.text_test_sdcard);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_sdcard_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_sdcard);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_sdcard_test);
                    break;
                case "SIMcard Test":
                    mAdapterMap.put("Name", R.string.text_test_simcard);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_simcard_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_simcard);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_simcard_test);
                    break;
                case "LightSensor Test":
                    mAdapterMap.put("Name", R.string.text_test_lightsensor);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_lightsensor_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_lightsensor);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_lightsensor_test);
                    break;
                case "Proximity Test":
                    mAdapterMap.put("Name", R.string.text_test_proximity);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_proximity_test);
                    // gaohui@wind-mobi.com 2016/04/07 add begin
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_proximity);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_proximity_test);
                    break;
                case "Fingerprint Test":
                    mAdapterMap.put("Name", R.string.title_test_fp);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_fingerprint_test);
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_fingerprint);
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_fingerprint_test);
                    break;

                default:
                    mAdapterMap.put("Name", R.string.text_test_ecompass);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_ecompass_test);
                    // gaohui@wind-mobi.com 2016/04/07 add start
                    mTestModuleStr.put(module.getEnName(),R.string.str_test_ecompass);
                    // gaohui@wind-mobi.com 2016/04/07 add end
                    mTestModuleImage.put(module.getEnName(),R.mipmap.asus_diagnostic_img_ecompass_test);
                    break;
            }
            mApapterList.add(mAdapterMap);
            //  Log.d(TAG, "add to mAdapter :"+module.getEnName());
        }
        Log.d(TAG, "loadResource finished , return the AdapterList");
        mIsLoadModuleImage = true;
        return mApapterList;
    }


    /**
     * 加载测试单元的启动方法
     * @param className
     * @return
     */
    public HashMap getMethod(String className){
        ArrayList <Object> obj = new ArrayList<Object>();
        ArrayList <Method> methods = new ArrayList<Method>();
        HashMap<String,List> res = new HashMap<String,List>();
        Method method = null;
        try {
            Class cls=Class.forName(className);
            Object o = cls.newInstance();
            Class paramType[] =new Class[0];
            method=cls.getMethod("startTest",new Class[]{});

            obj.add(o);
            methods.add(method);
            res.put("object", obj);
            res.put("method",methods);
            Log.d(TAG,"get the Method :"+method);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    public int getErrorCode(String moduleCode){
        for (TestCase testCase:mTestModule){
            if (testCase.getEnName().equals(moduleCode))
                return mTestcases.get(moduleCode).getErrorCode();
        }
        return 0000;
    }

    public String getEnName(String moduleCode){
        for (TestCase testCase:mTestModule){
            if (testCase.getEnName().equals(moduleCode))
                return mTestcases.get(moduleCode).getEnName();
        }
        return "";
    }

}