package com.asus.atd.smmitest.testcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by zhangtao on 2016/1/9.
 */
public class BatteryInfoTesting extends BaseActivity {


    private TextView batteryVoltage;
    private TextView batteryElectric;
    private TextView batteryTemerature;
    private TextView batteryStatus;
    private TextView batteryplugeType;
    private TextView batteryHealth;
    private TextView batteryLevel;
    private ModuleManager modul;
    private static final String moduleName = "Battery Test";
    public static int mSucCount = 0;
    public static int mFailCount = 0;




    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_battery);
      //  setTitle(R.string.title_test_battery);
        this.getActionBar().hide();
    }


    //初始化
    @Override
    protected void findViewById() {
        batteryHealth = (TextView) findViewById(R.id.tv_health_battery);
        batteryVoltage = (TextView) findViewById(R.id.tv_voltage_battery);
        batteryElectric = (TextView) findViewById(R.id.tv_electric_battery);
        batteryTemerature = (TextView) findViewById(R.id.tv_temerature_battery);
        batteryStatus = (TextView) findViewById(R.id.tv_status_battery);
        batteryplugeType = (TextView) findViewById(R.id.tv_powerType_battery);
        batteryLevel = (TextView) findViewById(R.id.tv_level_battery);
    }

    //注册广播
    private void regeistReceiver() {
        //huangzhijian@wind-mobi.com add 20161103 start
        mregisterOnce=true;
        //huangzhijian@wind-mobi.com add 20161103 end
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }


    //handler来与外部交互
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    batteryStatus.setText(getResources().getString(R.string.status_text_battery) + getResources().getString(R.string.batteryStatus_text_battery));
                    batteryHealth.setText(getResources().getString(R.string.health_test_battery) + getResources().getString(R.string.unknown_text_battery));
                    batteryTemerature.setText(getResources().getString(R.string.temerature_text_battery) + getResources().getString(R.string.unknown_text_battery));
                    batteryplugeType.setText(getResources().getString(R.string.powerType_text_battery) + getResources().getString(R.string.unknown_text_battery));
                    batteryElectric.setText(getResources().getString(R.string.electric_text_battery) + getResources().getString(R.string.unknown_text_battery));
                    batteryVoltage.setText(getResources().getString(R.string.voltage_test_battery) + getResources().getString(R.string.unknown_text_battery));
                    Log.d(TAG, "handler0");
                    break;
                case STATUS:
                    Log.d(TAG, "handlerStatus");
                    if (FLAG == false) {
                        mSucCount++;
                        batteryStatus.setText(getResources().getString(R.string.status_text_battery) + statusString);
                        batteryHealth.setText(getResources().getString(R.string.health_test_battery) + healthStatus);
                        batteryVoltage.setText(getResources().getString(R.string.voltage_test_battery) + voltage + " " + "mv");
                        batteryTemerature.setText(getResources().getString(R.string.temerature_text_battery) + temerature + "℃");
                        batteryElectric.setText(getResources().getString(R.string.electric_text_battery) + scale);
                        batteryLevel.setText(getResources().getString(R.string.level_text_battery) + level);
                        batteryplugeType.setText(getResources().getString(R.string.powerType_text_battery) + plugeString);
                        setTestResult(Constants.TEST_PASS, mSucCount);
                        finishActivity(Constants.TEST_PASS);
                    }
                    break;

                case FEAIL:

                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                    break;
                default:
                    break;
            }
        }
    };

    private static final int STATUS = 104;
    private int voltage;
    private String healthStatus;
    private String statusString;
    private int temerature;
    private int level, scale;
    private int plugeType;
    private String plugeString;
    private Boolean FLAG = false;
    //huangzhijian@wind-mobi.com add 20161103 start
    public boolean mregisterOnce=true;
    //huangzhijian@wind-mobi.com add 20161103 end


    private static final int FEAIL = 101;

    //广播的定义
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            handler.sendEmptyMessage(0);
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                getStatus(intent);
                getHealth(intent);
                getExtra(intent);
                getPowerType();
                //huangzhijian@wind-mobi.com add 20161103 start
                //handler.sendEmptyMessageDelayed(STATUS, 500);
                if(mregisterOnce){
                    mregisterOnce=false;
                    handler.sendEmptyMessageDelayed(STATUS, 500);
                }
                //huangzhijian@wind-mobi.com add 20161103 end
            }
        }
    };


    //设置初始状态
    private void clearData() {
        batteryStatus.setText(R.string.disconnect_text_battery);
        batteryHealth.setText(R.string.unknown_text_battery);
        batteryVoltage.setText(R.string.unknown_text_battery);
        batteryElectric.setText(R.string.unknown_text_battery);
        batteryTemerature.setText(R.string.unknown_text_battery);
        batteryplugeType.setText(R.string.unknown_text_battery);
        FLAG = false;
    }

    public void finishActivity(int resultCode) {

        if (MainManager.getInstance(this).mIsAutoTest) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, moduleName);
            finish();

        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, moduleName);
            setResult(resultCode);
            finish();
        }
    }

    public void setTestResult(int code, int count) {

        for (TestCase module : modul.mTestModule) {
            if (module.getEnName().equals("Battery Test")) {
                if (code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    @Override
    public void startTest() {
        super.startTest();
        clearData();
    }

    @Override
    protected void onResume() {

        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        regeistReceiver();
        startTest();


    }
           //处理电池健康状态
    private void getHealth(Intent intent) {

        int health = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthStatus = "COLD";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthStatus = "OVERHEAT";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthStatus = "DEAD";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthStatus = "GOOD";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthStatus = "OVER_VOLTAGE";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthStatus = "UNKONW_FAILURE";
                break;

            default:
                break;
        }
    }

    //获取当前电池的使用状态
    private void getStatus(Intent intent) {

        int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        plugeType = intent.getIntExtra("plugged", 0);

        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {

            if (plugeType > 0) {
                if (plugeType == BatteryManager.BATTERY_PLUGGED_AC) {
                    statusString = "CHARGING_AC";
                } else if (plugeType == BatteryManager.BATTERY_PLUGGED_USB) {
                    statusString = "CHARGING_USB";
                } else if (plugeType == BatteryManager.BATTERY_PLUGGED_WIRELESS) ;
                statusString = "CHARGING_WIRELESS";
            }
        } else {
            if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                statusString = "DISCHARGING";
            } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                statusString = "NOT_CHARGING";
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                statusString = "FULL";
            } else {
                statusString = "UNKNOWN";
            }
        }
    }

    private void getPowerType() {

        switch (plugeType) {
            case 0:
                plugeString = "power unnplugged";
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugeString = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugeString = "USB";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                plugeString = "WIRELESS";
                break;
            case (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB):
                plugeString = "AC" + " AND " + "USB";
                break;
            default:

                break;
        }
    }

    private void getExtra(Intent intent) {
        voltage = intent.getIntExtra("voltage", 0);

        scale = intent.getIntExtra("scale", 0);

        level = intent.getIntExtra("level", 0);

        temerature = intent.getIntExtra("temperature", 0);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        modul = ModuleManager.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(batteryReceiver);

    }

    @Override
    public void onBackPressed() {


        FLAG = true;
        handler.removeMessages(STATUS);
        handler = null;
        mFailCount++;
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED, moduleName);
        setTestResult(Constants.TEST_FAILED, mFailCount);
        setResult(Constants.TEST_FAILED);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
