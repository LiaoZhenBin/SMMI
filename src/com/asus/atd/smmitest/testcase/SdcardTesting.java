package com.asus.atd.smmitest.testcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;

/**
 * Created by luyuan on 2016/1/11.
 */
//实现SDcard测试模块
public class SdcardTesting extends BaseActivity {

    TextView statetext;
    TextView t1;
    TextView t2;
    TextView t3;

    String status;
    String total;
    String available;
    String used;

    public static int  mSucCount=0;
    public static int mFailCount=0;
    StatFs stat;

    static long blockSize = 0;
    static long totalBlocks = 0;
    static long availableBlocks = 0;
    private static final String mModuleName = "SDcard Test";
    private ModuleManager mModuleManager ;
   /* private Button pass_btn;
    private Button fail_btn;*/

    protected static final String LOG_TAG ="Factory_SDcardTest";

    protected void setContentView() {
        setContentView(R.layout.activity_testing_sdcard);
    }

    protected void findViewById() {
        statetext=(TextView)findViewById(R.id.tv_status_sd);
        t1 = (TextView) findViewById(R.id.tv_total_sd);
        t2 = (TextView) findViewById(R.id.tv_used_sd);
        t3 = (TextView) findViewById(R.id.tv_available_sd);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(LOG_TAG, getString(R.string.action) + action);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                if (stat == null) {
                    Log.d(LOG_TAG, getString(R.string.stat_null));
                     String externalPath = getExternalPath();
                    if (!"".equals(externalPath)) {
                        stat = new StatFs(externalPath);
                    }
                }
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availableBlocks = stat.getAvailableBlocks();
                updateScreen();
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                blockSize = 0;
                totalBlocks = 0;
                availableBlocks = 0;
                updateScreen();
            } else if (action.equals(Intent.ACTION_MEDIA_CHECKING)) {
                updateScreen();
            } else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
                updateScreen();
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                blockSize = 0;
                totalBlocks = 0;
                availableBlocks = 0;
                updateScreen();
            }
        }
    };
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        statetext = (TextView) findViewById(R.id.tv_status_sd);
        clearStatus();
        String externalPath = Environment.getExternalStorageDirectory().getPath();
        Log.d(LOG_TAG, getString(R.string.externalPath) + externalPath);

        if(!"".equals(externalPath)){
            try{
                stat = new StatFs(externalPath);
            }catch(Exception e){
                Toast.makeText(this, R.string.no_sd_device, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, R.string.no_sd_device, Toast.LENGTH_LONG).show();
        }
        if(stat != null){
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }else{
            statetext.setText(getString(R.string.status_test_sd)+getString(R.string.sd_unmounted));
            blockSize = 0;
            totalBlocks = 0;
            availableBlocks = 0;
        }
        total = formatSize(totalBlocks * blockSize);
        available = formatSize(availableBlocks * blockSize);
        used = formatSize((totalBlocks - availableBlocks) * blockSize);

        t1 = (TextView) findViewById(R.id.tv_total_sd);
        t1.setText(getString(R.string.total_test_sd) + total);


        t2 = (TextView) findViewById(R.id.tv_used_sd);
        t2.setText(getString(R.string.used_test_sd) + used);

        t3 = (TextView) findViewById(R.id.tv_available_sd);
        t3.setText(getString(R.string.available_test_sd) + available);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);

        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);

        if(hasSdCard()){
            handler.sendEmptyMessageDelayed(1,2000);
        }else{
            handler.sendEmptyMessageDelayed(2,5000);
        }
//        pass_btn=(Button)findViewById(R.id.pass_btn);
//        fail_btn=(Button)findViewById(R.id.fail_btn);
//        pass_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            	mSucCount++;
//                setTestResult(Constants.TEST_PASS, mSucCount);
//                finishActivity(Constants.TEST_PASS);
//            }
//        });
//        fail_btn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				 mFailCount++;
//		         setTestResult(Constants.TEST_FAILED,mFailCount);
//				 finishActivity(Constants.TEST_FAILED);
//			}
//		});
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mSucCount++;
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                    break;
                case 2:
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                default:
                    break;
            }

        }
    };

    private boolean hasSdCard() {
        if (Environment.getExternalStorageState(new File(getExternalPath()))
                .equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

	private String getExternalPath() {
		String externalPath = "";
		StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[] volumes = mStorageManager.getVolumeList();
		for (StorageVolume volume : volumes) {
			if (volume.isRemovable()
					&& Environment.MEDIA_MOUNTED.equals(mStorageManager
							.getVolumeState(volume.getPath()))) {
				externalPath = volume.getPath();
				break;
			}
		}
		return externalPath;
	}
	
    private void clearStatus() {
        t1.setText(R.string.total_test_sd);
        t2.setText(getString(R.string.used_test_sd));
        t3.setText(getString(R.string.available_test_sd));
    }
    @Override
    protected void onResume() {
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        updateScreen();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void updateScreen()  {

        if (hasSdCard()) {
            status = Environment.getExternalStorageState();
            statetext.setText("" + status);

            total = formatSize(totalBlocks * blockSize);
            available = formatSize(availableBlocks * blockSize);
            used = formatSize((totalBlocks - availableBlocks) * blockSize);

            t1.setText(getString(R.string.total_test_sd) + total);
            t2.setText(getString(R.string.used_test_sd) + used);
            t3.setText(getString(R.string.available_test_sd) + available);
           // pass_btn.setVisibility(View.VISIBLE);
        } else {
            statetext.setText(getString(R.string.sd_unmounted));
            t1.setText(getString(R.string.total_test_sd));
            t2.setText(getString(R.string.used_test_sd));
            t3.setText(getString(R.string.available_test_sd));
           // pass_btn.setVisibility(View.GONE);
        }
    }

    private String formatSize(long size) {

        String suffix = null;
        if (size <= 0)
            return "0";
        if (size >= 1024) {
            suffix = "K";
            size /= 1024;
            if (size >= 1024) {
                suffix = "M";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);

        return resultBuffer.toString();
    }
    /**
     * 测试正常结束返回
     * 根据是单项测试/自动测试：
     * 自动测试 --- MainManager.getInstance().sendFinishBroadcast()
     * 单项测试 --- 更新测试结果
     * @param resultCode
     */
    public void finishActivity(int resultCode){
        if (MainManager.getInstance(this).mIsAutoTest){
            MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
            finish();
            Log.d(TAG, "BluetoothTest finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
            setResult(resultCode);
            finish();
        }
    }
    @Override
    protected void setListener() {

    }

    /**
     * 设置测试返回信息
     * @param code
     * @param count
     */
    public void setTestResult(int code,int count){
        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals(mModuleName)){
                if(code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }
    @Override
    protected void init() {
        mModuleManager = ModuleManager.getInstance(this);
        this.setTitle(mModuleName);
    }
}

