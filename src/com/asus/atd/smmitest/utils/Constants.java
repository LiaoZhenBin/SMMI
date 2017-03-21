package com.asus.atd.smmitest.utils;

/**
 * Created by lizusheng on 2015/12/28.
 */
public class Constants {
    public static final String PREF_KEY_TEST_RESULT_FILE_NAME = "SMMI_TestResult.txt";
    public static final String PREF_NAME_TEST_RESULTS = "emode_results";
    public static final String PREF_KEY_TEST_RESULTS_BOARD = "emode_board_results";
    public static final String PREF_KEY_TEST_RESULTS_PHONE = "emode_phone_results";
    public static final String PREF_KEY_TEST_RESULT_FILE_INITIALIZED = "test_result_file_initialized";

    public static final String ACTION_EMODE_UPDATE_RESULT_FILE = "com.wind.intent.action.EMODE_UPDATE_RESULT_FILE";
    public static final String ACTION_EMODE_INIT_RESULT_FILE = "com.wind.intent.action.EMODE_INIT_RESULT_FILE";
    public static final String ACTION_EMODE_RESTORE_BT = "com.wind.intent.action.EMODE_RESTORE_BT";
    public static final String ACTION_MODULT_TEST_RETURN ="action_module_test_return";

    public static final String ACTION_EMODE ="com.wind.intent.action.EMODE";
    public static final String EXTRA_EMODE_CODE ="com.wind.intent.EXTRA_EMODE_CODE";

    public static final String KEY_TEST_FINISH = "key_test_finish";

    public static final String BROADCAST_MODULE_TEST = "com.wind.emode.BROADCAST_MODULE_TEST";
    public static final String BROADCAST_UPLOAD_RESPONSE = "com.smmitest.UPLOADDATA_RESPONSE";
    public static final int TEST_FINISH = 0x0100 ;

    public static final int NOT_TEST = -1;
    public static final int TEST_PASS = 0 ;
    public static final int TEST_FAILED = 1;

    public static final int TESTCASE_TYPE_PHONE = 0x01;
    public static final int TESTCASE_TYPE_BOARD = 0x02;
    public static final int TESTCASE_TYPE_OTHER = 0x04;
    public static final int TESTCASE_TYPE_CUSTOM = 0x08;
    public static final int TESTCASE_TYPE_DEFAULT = 0x10;

    public static final int MAIN_CAMERA_TEST = 0x00;
    public static final int FRONT_CAMERA_TEST = 0x01;

}
