package com.asus.atd.smmitest.main;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.SharedPreferences;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.base.CommonAdapter;
import com.asus.atd.smmitest.base.ViewHolder;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.FileUtils;
import com.asus.atd.smmitest.utils.SPUtils;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.view.DrawView;
import com.asus.atd.smmitest.R;
import com.asus.atd.smmitest.utils.UploadTask;
import com.asus.atd.smmitest.utils.UploadUtils;


//laiozhenbin@wind-mobi.com  2016/1/15 add begain
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import android.view.ViewGroup;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Handler;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.Spinner;


//zhangtao@wind-mobi.com  2016/1/15 add end
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

//liaozhenbin@wind-mobi.com 20160717 add begin
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.ArrayList;
//liaozhenbin@wind-mobi.com 20160717 add end

/**
 * Created by lizusheng on 2015/12/25.
 */
public class MAIN extends BaseActivity {
    private GridView gridView;
    private ArrayList<HashMap<String, Integer>> mApapterList;
    private CommonAdapter<HashMap<String, Integer>> mCommonAdapter;
    private static final int INDEX_TEST_ALL = 0;
    private static final int INDEX_TEST_SINGLE = 1;
    private static final int INDEX_UPLOAD = 2;
    private static final int INDEX_HELP = 3;
    private static MainManager mMainManager;
    //    liaozhenbin@wind-mobi.com  2016/1/15 begain
    private TextView cpuNum, batteryNum, cpuRight, batteryRight;
    private ImageView cpuImage, batteryImage;
//    liaozhenbin@wind-mobi.com  2016/1/15 add end


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        android.app.ActionBar mActionbar = this.getActionBar();
        mActionbar.setDisplayShowHomeEnabled(false);
        mActionbar.setDisplayHomeAsUpEnabled(false);
        mActionbar.setDisplayShowCustomEnabled(true);
        mActionbar.setCustomView(R.layout.layout_actionbar_main);
        checkAndRequestPermission();
    }

    @Override
    protected void findViewById() {
        gridView = (GridView) findViewById(R.id.gridView_main);
//      liaozhenbin@wind-mobi.com  2016/1/15 add begain
        cpuNum = (TextView) findViewById(R.id.text_cpu_num);
        batteryNum = (TextView) findViewById(R.id.text_battery_num);
        cpuRight = (TextView) findViewById(R.id.text_cpu_text);
        batteryRight = (TextView) findViewById(R.id.text_battery_text);
        cpuImage = (ImageView) findViewById(R.id.image_cpu_id);
        batteryImage = (ImageView) findViewById(R.id.image_battery_id);
//    liaozhenbin@wind-mobi.com  2016/1/15 add end
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void init() {
        mMainManager = MainManager.getInstance(this);
        mApapterList = new ArrayList<HashMap<String, Integer>>();
        HashMap<String, Integer> mAdapterMap = new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_test_all_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_test_all_n);
        mApapterList.add(mAdapterMap);

        mAdapterMap = new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_test_single_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_single_test_n);
        mApapterList.add(mAdapterMap);

        mAdapterMap = new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_upload_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_repair_n);
        mApapterList.add(mAdapterMap);

        mAdapterMap = new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_help_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_help_n);
        mApapterList.add(mAdapterMap);


        mCommonAdapter = new CommonAdapter<HashMap<String, Integer>>(this, mApapterList, R.layout.item_gridview_main) {
            @Override
            public void convert(ViewHolder holder, HashMap<String, Integer> item) {
                holder.setImageResource(R.id.iv_gv_main, item.get("Image"));
                holder.setText(R.id.tv_gv_main, getString(item.get("Name")));
            }
        };
        gridView.setAdapter(mCommonAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case INDEX_TEST_ALL:
                        mMainManager.mIsAutoTest = true;
                        mMainManager.startAllTest();
                        break;
                    case INDEX_TEST_SINGLE:
                        mMainManager.mIsAutoTest = false;
                        startActivity(new Intent(MAIN.this, SingleTestActivity.class));
                        break;
                    case INDEX_UPLOAD:
                        showUploadDialog();
                        break;
                    case INDEX_HELP:
                        //xuyi@wind-mobi.com 20170119 add for bug#152388 begin
                        showHelpDialog();
                        //xuyi@wind-mobi.com 20170119 add for bug#152388 end
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FileUtils.verifyStoragePermissions(this);
        IntentFilter mIntentFilter = new IntentFilter(Constants.BROADCAST_UPLOAD_RESPONSE);
        registerReceiver(mUploadResponseReceiver, mIntentFilter);
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
//  liaozhenbin@wind-mobi.com  2016/1/15 add begain
        start();
//  liaozhenbin@wind-mobi.com  2016/1/15  add end
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUploadResponseReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_test_result:
                startActivity(new Intent(MAIN.this, ResultActivity.class));
                break;
            case R.id.action_clear_cache:
                clearCache(this);
                break;
            case R.id.action_reset:
                // liaozhenbin@wind-mobi.com 2016/01/29 add start
                showResetDialog();
                // liaozhenbin@wind-mobi.com 2016/01/29 add end
                break;
            case R.id.action_about:
                //liaozhenbin@wind-mobi.com 201601026 add begin
                startActivity(new Intent(MAIN.this, AboutActivity.class));
                //liaozhenbin@wind-mobi.com 201601026 add end
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    BroadcastReceiver mUploadResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_UPLOAD_RESPONSE)) {
                Log.d(TAG, "Receive a broadcast from service for uploadData ");
                String response = intent.getStringExtra("strResult");
                Log.d(TAG, "the value from service :" + response);
                if (response.equals(getString(R.string.msg_upload_response_success))) {
                    showMesDialog(MAIN.this, response + "\n" + getString(R.string.msg_upload_response_success_reset));
                    SPUtils.put(MAIN.this, "emode_phone_results", "", Constants.PREF_NAME_TEST_RESULTS);
                } else {
                    showMesDialog(MAIN.this, response);
                }
            }
        }
    };

    /**
     * show Upload Dialog
     */
    private void showUploadDialog() {
        Log.d(TAG, "res:" + UploadUtils.checkHasTest(this));
        if (!UploadUtils.checkHasTest(this)) {
            //liaozhenbin@wind-mobi.com 20160721 add begin
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            //liaozhenbin@wind-mobi.com 20160721 add end
            dialog.setMessage(getString(R.string.msg_upload_nottest));
            dialog.setNeutralButton(getString(R.string.msg_upload_dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.create().show();
            return;
        }

        if (!UploadUtils.isNetworkConnected(this)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.msg_upload_dialog_title_nonetwork));
            dialog.setMessage(getString(R.string.msg_upload_dialog_mes_nonetwork));
            dialog.setNeutralButton(getString(R.string.msg_upload_dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.create().show();
            return;
        }

        List listResult = UploadUtils.getResultForUpload(this);
        String mesContext = "";
        AlertDialog.Builder uploadDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        uploadDialog.setTitle(getString(R.string.title_upload_dialog));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setMinimumHeight(500);
        linearLayout.setOrientation(1);
        TextView tv_result = new TextView(this);
        tv_result.setTextColor(Color.WHITE);

        tv_result.setTextSize(17);
        mesContext = getString(R.string.text_upload_dialog_result) + String.valueOf(listResult.get(0)) + "\n";
        String errorCode = String.valueOf(listResult.get(1));
        mesContext = mesContext + getString(R.string.text_upload_dialog_errorcode) + errorCode + "\n\n";
        mesContext = mesContext + getString(R.string.text_upload_dialog_ssn);
        tv_result.setText(mesContext);
        linearLayout.addView(tv_result);

        final EditText et_SSN = new EditText(this);
        et_SSN.setHint("SSN");
        et_SSN.setTextColor(Color.WHITE);
        et_SSN.setText(UploadUtils.getSSN());
        //et_SSN.setText("C5OKAS012945");
        et_SSN.setSingleLine();
        et_SSN.setInputType(4096);
        linearLayout.addView(et_SSN);

        final EditText et_ID = new EditText(this);
        et_ID.setHint(getString(R.string.hint_upload_dialog_id));
        et_ID.setTextColor(Color.WHITE);
        //et_ID.setText("Eason");
        et_ID.setSingleLine();
        et_ID.setInputType(4096);
        linearLayout.addView(et_ID);

        final Spinner spinner = new Spinner(this);
        final String[] strOfSpinner = {"---Select Server---", "AS", "AM", "AU", "CN", "EU"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_upload_spinner, strOfSpinner);
        spinner.setAdapter(arrayAdapter);
        linearLayout.addView(spinner);

        uploadDialog.setView(linearLayout);
        uploadDialog.setNegativeButton(getString(R.string.msg_upload_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        uploadDialog.setNeutralButton(getString(R.string.msg_upload_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String serial_no = et_SSN.getText().toString().toUpperCase();
                String test_id = et_ID.getText().toString().toUpperCase();
                String servicer = spinner.getSelectedItem().toString().toLowerCase();
                String product_type = UploadUtils.getProductType(serial_no);
                String test_result = (String) UploadUtils.getResultForUpload(MAIN.this).get(0);
                String test_date = UploadUtils.getTestDateTime()[0];
                String test_time = UploadUtils.getTestDateTime()[1];
                String problem_code = (String) UploadUtils.getResultForUpload(MAIN.this).get(1);
                String pass_item = (String) UploadUtils.getResultForUpload(MAIN.this).get(2);
                String isn = UploadUtils.getISN();
                Boolean isSingleTest = (!MainManager.getInstance(MAIN.this).mIsAutoTest);
                String wtp_ver = UploadUtils.getToolVersion(MAIN.this);
                String os_version = UploadUtils.getOS_Version();
                Log.d(TAG, "");
                if (serial_no.isEmpty() || test_id.isEmpty() || spinner.getSelectedItemPosition() == 0) {
                    showMesDialog(MAIN.this, getString(R.string.msg_upload_input_error));
                    return;
                }

                String mHttpString = "https://" + servicer + ".eservice.asus.com/remote/RemoteConnection?action=ndss_upload" +
                        "&serial_no=" + serial_no + "&product_type=" + product_type + "&test_result=" + test_result + "&test_id=" + test_id +
                        "&test_date=" + test_date + "&test_time=" + test_time + "&problem_code=" + problem_code +
                        "&pass_item=" + pass_item + "&isn=" + isn + "&ext2=" + isSingleTest.toString() +
                        "&wtp_ver=" + wtp_ver + "&os_version=" + os_version + "\n";
                Log.d(TAG, "HttpString:" + mHttpString);
                UploadTask mUploadTesk = new UploadTask(MAIN.this);
                mUploadTesk.execute(mHttpString);

            }
        });
        uploadDialog.create().show();
    }

    //xuyi@wind-mobi.com 20170119 add for bug#152388 begin
    private void showHelpDialog() {
        Toast.makeText(this, R.string.help_coming_soon, Toast.LENGTH_LONG).show();
    }
    //xuyi@wind-mobi.com 20170119 add for bug#152388 end


    private void showMesDialog(Context context, String mes) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(mes);
        dialog.setNeutralButton(getString(R.string.msg_upload_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.create().show();
    }


    /**
     * 显示RESET选择对话框
     */
    private void showResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Reset");
        builder.setMessage("Tap reset ro clear all of the test result?");

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("emode_phone_results", "");
                editor.commit();
                FileUtils.deleteTestResult();

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // liaozhenbin@wind-mobi.com 2016/01/29 add end


    @Override
    public void onClick(View view) {

    }


//    liaozhenbin@wind-mobi.com  2016/1/15  add begain

    private long idel;
    private long totalCpu;
    private int batteryLevel;


    private int batteryScale = -1;
    private static final int CPU_FLAG = 10;
    private static final int BATTERY_FLAG = 20;
    private boolean FLAG = true;
    private int imageHeight;
    private int textHeight;
    private long cpuUsage;
    private int batteryLast;
    private DrawView drawView;

    private void start() {
        imageHeight = 69;
        textHeight = 145;
        FLAG = true;

        registMyReceiver();
        new Thread() {
            @Override
            public void run() {
                while (FLAG) {
                    cpuUsage = getCpuUsage();
                    drawView.getData(getMemoryUsageRate());
                    try {
                        Thread.sleep(700);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }

            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FLAG = false;
        //hubo@wind-mobi.com mod Bug#150588 2017/01/10 begin
        //unregisterReceiver(myBatteryReceiver);
        if (myBatteryReceiver != null) {
            unregisterReceiver(myBatteryReceiver);
        }
        //hubo@wind-mobi.com mod Bug#150588 2017/01/10 end
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

                getDataAndColor((int) cpuUsage, CPU_FLAG);
                getDataAndColor(getBatteryLevel(batteryLevel, batteryScale), BATTERY_FLAG);
                setImageHeight(BATTERY_FLAG, getBatteryLevel(batteryLevel, batteryScale));
                setImageHeight(CPU_FLAG, (int) cpuUsage);
                cpuNum.setText(cpuUsage + "");
                batteryNum.setText(getBatteryLevel(batteryLevel, batteryScale) + "");
            }
        }
    };

    private void setImageHeight(int which, int num) {

        int currentImageHeight = 0;


        if (which == CPU_FLAG) {
            currentImageHeight = imageHeight + textHeight * (100 - num) / 100;

            ViewGroup.LayoutParams layout = cpuImage.getLayoutParams();
            layout.height = currentImageHeight;


        } else if (which == BATTERY_FLAG) {
            currentImageHeight = imageHeight + textHeight * (100 - num) / 100;


            ViewGroup.LayoutParams layout = batteryImage.getLayoutParams();
            layout.height = currentImageHeight;


        }

    }


    private void registMyReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myBatteryReceiver, filter);
    }


    private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

                batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            }
        }
    };

    private int getBatteryLevel(int level, int scale) {

        int num = 100 * level / scale;

        return num;
    }

    //获取CPU的使用时间
    private long getCpuUsage() {
        getTotalCpuTime();

        float totalCpuTime1 = totalCpu;
        float idle1 = idel;
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getTotalCpuTime();
        float totalCpuTime2 = totalCpu;
        float idle2 = idel;

        long cpuUsage = (long) (100 * ((totalCpuTime2 - totalCpuTime1) - (idle2 - idle1)) / (totalCpuTime2 - totalCpuTime1));

        return cpuUsage;
    }

    public void getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        idel = Long.parseLong(cpuInfos[5]);

    }

    //设置并选择颜色
    private void getDataAndColor(int num, int which) {


        if (which == BATTERY_FLAG) {


            if (num >= 75 && num <= 100) {
                batteryNum.setTextColor(getResources().getColor(R.color.green_mine));
                batteryRight.setTextColor(getResources().getColor(R.color.green_mine));
            } else if (num >= 50 && num < 75) {
                batteryNum.setTextColor(getResources().getColor(R.color.blue_mine));
                batteryRight.setTextColor(getResources().getColor(R.color.blue_mine));
            } else if (num > 20 && num < 50) {
                batteryNum.setTextColor(getResources().getColor(R.color.yello_mine));
                batteryRight.setTextColor(getResources().getColor(R.color.yello_mine));
            } else {
                batteryNum.setTextColor(getResources().getColor(R.color.red_mine));
                batteryRight.setTextColor(getResources().getColor(R.color.red_mine));
            }
        } else if (which == CPU_FLAG) {

            if (num >= 0 && num <= 20) {
                cpuNum.setTextColor(getResources().getColor(R.color.green_mine));
                cpuRight.setTextColor(getResources().getColor(R.color.green_mine));
            } else if (num <= 50 && num > 20) {
                cpuNum.setTextColor(getResources().getColor(R.color.blue_mine));
                cpuRight.setTextColor(getResources().getColor(R.color.blue_mine));
            } else if (num > 50 && num <= 80) {
                cpuNum.setTextColor(getResources().getColor(R.color.yello_mine));
                cpuRight.setTextColor(getResources().getColor(R.color.yello_mine));
            } else {
                cpuNum.setTextColor(getResources().getColor(R.color.red_mine));
                cpuRight.setTextColor(getResources().getColor(R.color.red_mine));
            }
        }
    }
//    liaozhenbin@wind-mobi.com  2016/1/15 add end


    //    huangzhijian@wind-mobi.com  2016/01/18 add start
    public int getMemoryUsageRate() {
        int memoryUsageRate = 0;
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        Log.d("MEMORY", "MEMORY:memSize" + memSize);

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        Log.d("MEMORY", "MEMORY:initial_memory" + initial_memory);
        memoryUsageRate = 100 - (int) (100 * memSize / initial_memory);

        return memoryUsageRate;
    }
//    huangzhijian@wind-mobi.com  2016/01/18 add end

    //heliguang@wind-mobi.com 20160221 add begin
    private static void clearCache(Context context) {
        deleteFilesByDiretory(context.getCacheDir());
        deleteFilesByDiretory(context.getExternalCacheDir());
    }

    private static void deleteFilesByDiretory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
    //heliguang@wind-mobi.com 20160221 add end

    //liaozhenbin@wind-mobi.com 20160717 add begin

    private static final int REQUEST_PERMISSIONS = 1;

    public void checkAndRequestPermission() {
        final List<String> permissionsList = new ArrayList<String>();

        addPermission(permissionsList, Manifest.permission.CAMERA);
        addPermission(permissionsList, Manifest.permission.RECORD_AUDIO);
        //liaozhenbin@wind-mobi.com 20160721 add begin
        addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE);
        addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);
        //liaozhenbin@wind-mobi.com 20160721 add end

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
        }
    }


    private void addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
    }

    //liaozhenbin@wind-mobi.com 20160717 add end
}
