package com.asus.atd.smmitest.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;

/**
 * Created by lizusheng on 2015/12/29.
 * Receive the instruction from DialerPad
 */
public class InstructionReceiver extends BroadcastReceiver {
    private static final String TAG = InstructionReceiver.class.getSimpleName();
    private Context context;
    private ModuleManager mModuleManager;
    private MainManager mMainManager;

    enum ENGINEER_CODE

    {
        A983A0B, //*983*0#
                A983A1B, //*983*1#
                ABAB9463A1BABA, //*#*#9463*1#*#*
                A983A7B, //*983*7#
                ABAB1705B, //*#*#1705#
                AB1923B, //*#1923#
                AB458B,  //*#458#
                A983A57B,  //*983*57#
                A983A233B,  //*983*233#
                A983A27274B,  //*983*27274#
                A983A3640B,  //*983*3640#
                A983A9999B,  //*983*9999#
                A9643A240B,  //*9643*240#
                UNKNOWN
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (mModuleManager == null) mModuleManager = ModuleManager.getInstance(context);
        if (mMainManager == null) mMainManager = MainManager.getInstance(context);
        String action = intent.getAction();
        String code = intent.getStringExtra(Constants.EXTRA_EMODE_CODE);
        Log.d(TAG, "Code:" + code);
        if (action.equals(Constants.ACTION_EMODE)) {
            if (code != null) {
                if (!mModuleManager.mXmlParsed) {
                    mModuleManager.parseXML();
                }
                clearDialerPad(context);
                startActivityByCode(code);
            }
        }
    }

    public void startActivityByCode(String code) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mModuleManager.mTestcases.get(code) != null) {
            String className = mMainManager.modulePackageName + ".testcase." + mModuleManager.mTestcases.get(code).getActivity() + "ing";
            intent.setComponent(new ComponentName(mMainManager.modulePackageName, className));
            context.startActivity(intent);
        } else {
            ENGINEER_CODE eCode = ENGINEER_CODE.UNKNOWN;
            try {
                eCode = ENGINEER_CODE.valueOf(code.replace('*', 'A').replace('#', 'B'));
            } catch (IllegalArgumentException e) {

            }
            switch (eCode) {
                case A983A0B:
                    intent.setComponent(new ComponentName(mMainManager.modulePackageName, mMainManager.modulePackageName + ".MainActivity"));
                    mModuleManager.mTestcaseType = Constants.TESTCASE_TYPE_BOARD;
                    context.startActivity(intent);
                    break;
                case A983A1B:
                case ABAB1705B:
                case AB1923B:
                case ABAB9463A1BABA:
                    intent.setComponent(new ComponentName(mMainManager.modulePackageName, mMainManager.modulePackageName + ".MainActivity"));
                    mModuleManager.mTestcaseType = Constants.TESTCASE_TYPE_PHONE;
                    context.startActivity(intent);
                    break;
                case A983A7B:
                case AB458B:
                    intent.setComponent(new ComponentName(mMainManager.modulePackageName, mMainManager.modulePackageName + ".SingleTestActivity"));
                    ModuleManager.mTestcaseType = Constants.TESTCASE_TYPE_OTHER;
                    context.startActivity(intent);
                    break;
                case A983A3640B:
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.RadioInfo"));
                    context.startActivity(intent);
                    break;
                case A983A57B:
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$PrivacySettingsActivity"));
                    context.startActivity(intent);
                    break;
                case A983A233B:
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.ApnSettings"));
                    context.startActivity(intent);
                    break;
                case A983A27274B:
                    PowerManager powermanager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    powermanager.reboot(null);
                    break;
                case A983A9999B:
                    intent.setComponent(new ComponentName("com.android.updatesystem", "com.android.updatesystem.UpdateSystemT"));
                    if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                        context.startActivity(intent);
                    }
                    break;
                case A9643A240B:
                    intent.setComponent(new ComponentName("com.wind.benqtestapp",
                            "com.wind.emode.benq.MainActivity"));
                    if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                        context.startActivity(intent);
                    }
                    break;
                case UNKNOWN:
                    Log.w(this, "TestCase not found: " + code);
                    break;
            }
        }
    }

    public void clearDialerPad(Context context) {
        Intent i = new Intent("com.wind.intent.action.CLEAR_DIALER_PAD");
        context.sendBroadcast(i);
    }
}
