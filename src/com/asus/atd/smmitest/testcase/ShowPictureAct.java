package com.asus.atd.smmitest.testcase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;
import android.graphics.Matrix;
/**
 * Created by lizusheng on 2016/1/8.
 */
public class ShowPictureAct extends BaseActivity{

	private static final String TAG = "ShowPictureAct";
	private ImageView mPicture;
	private Button btn_failed;
	private Button btn_pass;
	private int mCameraType;
	private String mModuleName;
	public static int mSucCountFont = 0;
	public static int mFailCountFront = 0;
	public static int mSucCountMain = 0;
    public static int mFailCountMain = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_showpicture);
	}

	@Override
	protected void findViewById() {
		if (findViewById(R.id.btn_pass) == null && findViewById(R.id.btn_fail )== null) {
			View v = LayoutInflater.from(this).inflate(R.layout.layout_show_picture, (ViewGroup) this.getWindow().getDecorView());
			btn_failed = (Button) v.findViewById(R.id.btn_fail);
			btn_pass = (Button) v.findViewById(R.id.btn_pass);
		}
		mPicture = (ImageView) findViewById(R.id.picture);
	}

	@Override
	protected void setListener() {
		btn_failed.setOnClickListener(this);
		btn_pass.setOnClickListener(this);
	}

	@Override
	protected void init() {
		mCameraType = getIntent().getIntExtra("CameraType",0);
		mModuleName =(mCameraType == Constants.MAIN_CAMERA_TEST)?getString(R.string.text_test_main_camera)
				:getString(R.string.text_test_front_camera);
		setTitle(mModuleName);
		String fileName = getIntent().getStringExtra(CameraTesting.KEY_FILENAME);
		Log.i(TAG, fileName);
		String path = getFileStreamPath(fileName).getAbsolutePath();
		Display display = getWindowManager().getDefaultDisplay();
		float destWidth = display.getWidth();
		float destHeight = display.getHeight();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;

		int inSampleSize = 1;
		if (srcHeight > destHeight || srcWidth > destWidth) {
			if (srcWidth > srcHeight) {
				inSampleSize = Math.round(srcHeight / destHeight);
			} else {
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();		
		int newWidth  = getWindowManager().getDefaultDisplay().getWidth() + 100;
		int newHeight = getWindowManager().getDefaultDisplay().getHeight(); 

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);	
		Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
				height, matrix, true); 	
		BitmapDrawable bDrawable = new BitmapDrawable(getResources(), newbitmap);
		mPicture.setImageDrawable(bDrawable);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_pass:
				 if(mCameraType == Constants.MAIN_CAMERA_TEST){
					 mSucCountMain++;
					 setTestResult(Constants.TEST_PASS,mSucCountMain);
				 }else {
					 mSucCountFont++;
					 setTestResult(Constants.TEST_PASS,mSucCountFont);
				 }
				finishActivity(Constants.TEST_PASS);
				break;
			case R.id.btn_fail:
				if(mCameraType == Constants.MAIN_CAMERA_TEST){
					mFailCountMain++;
					setTestResult(Constants.TEST_FAILED,mFailCountMain);
				}else {
					mFailCountFront++;
					setTestResult(Constants.TEST_FAILED,mFailCountFront);
				}
				finishActivity(Constants.TEST_FAILED);
				break;
		}
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
			Log.d(TAG, "Camera Test finished ,send broadcast to run the next test module");
		}else {
			Log.d(TAG, "Camera Test finished ："+resultCode);
			SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
			Intent i = new Intent(Constants.ACTION_MODULT_TEST_RETURN);
			i.putExtra("ResultCode", resultCode);
			sendBroadcast(i);
			finish();
		}
	}

	public void setTestResult(int code,int count){
		for (TestCase module: ModuleManager.getInstance(this).mTestModule){
			if (module.getEnName().equals(mModuleName)){
				Log.d(TAG, "setResult:" + mModuleName + count);
				if(code == Constants.TEST_PASS){
					module.setSuccessCount(count);
				}else if (code == Constants.TEST_FAILED){
					module.setFailCount(count);
				}
			}
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		if (!(mPicture.getDrawable() instanceof BitmapDrawable)) return;
		BitmapDrawable b = (BitmapDrawable) mPicture.getDrawable();

		b.getBitmap().recycle();
		mPicture.setImageDrawable(null);
		super.onDestroy();
	}

}
