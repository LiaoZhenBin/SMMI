package com.asus.atd.smmitest.testcase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
//add by sunxiaolong@wind-mobi.com for #149740 begin
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.ModuleManager;
import android.widget.Toast;
//add by sunxiaolong@wind-mobi.com for #149740 end

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.R;

/**
 * Created by lizusheng on 2016/1/8.
 */
public class CameraTesting extends BaseActivity implements SurfaceHolder.Callback {
	private static final String TAG = "CameraTesting";
	public static final String KEY_FILENAME = "CameraTestPic";
	private static int mCameraType = 0;
	private Button mTakePhoto;
	private View mViewTakePhoto;
	private SurfaceView mSurfaceView;
	private Camera mCamera;
	private String mFileName;
	private OrientationEventListener mOrEventListener;
	private Boolean mCurrentOrientation =false;
	private String mModuleName;
 //zhangtao@wind-mobi.com 2016/7/15 add begain
    private boolean btflag = false;
	//zhangtao@wind-mobi.com 2016/7/15 add end
	private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};

	private Camera.PictureCallback mJpeg = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mFileName = UUID.randomUUID().toString() + ".jpg";
			Log.i(TAG, mFileName);
			FileOutputStream out = null;
			try {
				out = openFileOutput(mFileName, Context.MODE_PRIVATE);
				byte[] newData = null;
				if (mCurrentOrientation) {
				    BitmapFactory.Options opts = new BitmapFactory.Options();
				    opts.inSampleSize = 3;
					Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, opts);
					Matrix matrix = new Matrix();
					if (mCameraType == Constants.MAIN_CAMERA_TEST) {
					    matrix.setRotate(90);
					} else if (mCameraType == Constants.FRONT_CAMERA_TEST){
					    matrix.setRotate(-90);
					}
					Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0,
							oldBitmap.getWidth(), oldBitmap.getHeight(),
							matrix, true);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					newBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
					newData = baos.toByteArray();
					out.write(newData);
				} else {
					out.write(data);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent i = new Intent(CameraTesting.this, ShowPictureAct.class);
			i.putExtra(KEY_FILENAME, mFileName);
			i.putExtra("CameraType", mCameraType);
			startActivity(i);
			finish();
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//     zhangtao@wind-mobi.com  2016/7/15 add begain
        btflag = false;
//        zhangtao@wind-mobi.com 2016/7/15 add end

	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_test_camera);
		this.getActionBar().hide();
	}

	@Override
	protected void findViewById() {
		if (findViewById(R.id.btn_takephoto) == null) {
			mViewTakePhoto = LayoutInflater.from(this).inflate(R.layout.layout_takephoto, (ViewGroup) this.getWindow().getDecorView());
		}
	}

	@Override
	protected void setListener() {
		mTakePhoto = (Button) mViewTakePhoto.findViewById(R.id.btn_takephoto);
		mTakePhoto.setOnClickListener(this);
	}

	@Override
	protected void init() {
		mCameraType = getIntent().getIntExtra("CameraType",0);
		setTitle(mModuleName =(mCameraType == Constants.MAIN_CAMERA_TEST)?getString(R.string.text_test_main_camera)
				:getString(R.string.text_test_front_camera));
		startOrientationChangeListener();
		mSurfaceView = (SurfaceView) findViewById(R.id.my_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Parameters parameters = mCamera.getParameters();
		Size largestSize = getBestSupportedSize(parameters
				.getSupportedPreviewSizes());
		parameters.setPreviewSize(largestSize.width, largestSize.height);
		//delete by sunxiaolong@wind-mobi.com for bug#151478 begin
//		largestSize = getBestSupportedSize(parameters
//				.getSupportedPictureSizes());
		//delete by sunxiaolong@wind-mobi.com for bug#151478 end
		parameters.setPictureSize(largestSize.width, largestSize.height);
		mCamera.setParameters(parameters);

		try {
			mCamera.startPreview();
		} catch (Exception e) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}

	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.setDisplayOrientation(90);  //旋转90度与手机竖屏方向一致
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_takephoto:
				  //zhangtao@wind-mobi.com  2016/7/7 modify  begain
                   //mCamera.takePicture(mShutter, null, mJpeg);
                if (!btflag) {
                    mCamera.takePicture(mShutter, null, mJpeg);
                    mTakePhoto.setClickable(false);
                    btflag = true;
                    //zhangtao@wind-mobi.com  2016/7/7 modify  end
                }

				break;
			default:
				break;
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
		//modify by sunxiaolong@wind-mobi.com for #149740 begin
		try {
			openCamera(mCameraType);
		} catch (Exception e) {
			String excStr = getString(R.string.str_camera_exception_subscription);
			if (mCameraType == 0) {
				excStr = "Main " + excStr;
			} else {
				excStr = "Front " + excStr;
			}
			Toast.makeText(this, excStr, Toast.LENGTH_LONG).show();
			setTestResult(Constants.TEST_FAILED, mCameraType);
			finishActivity(Constants.TEST_FAILED);
		}
		//modify by sunxiaolong@wind-mobi.com for #149740 end
	}

	//add by sunxiaolong@wind-mobi.com for #149740 begin
	public void finishActivity(int resultCode){
		if (com.asus.atd.smmitest.manager.MainManager.getInstance(this).mIsAutoTest){
			com.asus.atd.smmitest.manager.MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
			finish();
			Log.d(TAG, "Camera Test finished ,send broadcast to run the next test module");
		}else {
			Log.d(TAG, "Camera Test finished ："+resultCode);
			com.asus.atd.smmitest.utils.SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
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
	//add by sunxiaolong@wind-mobi.com for #149740 end

	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}


	public void openCamera(int cameraType) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(cameraType);  //0：后摄， 1：前摄
		} else {
			mCamera = Camera.open();
		}
	}


	private final void startOrientationChangeListener() {
		mOrEventListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int rotation) {
				if (((rotation >= 0) && (rotation <= 45)) || (rotation >= 315)
						|| ((rotation >= 135) && (rotation <= 225))) {
					mCurrentOrientation = true;
				} else if (((rotation > 45) && (rotation < 135))
						|| ((rotation > 225) && (rotation < 315))) {
					mCurrentOrientation = false;
				}
			}
		};
		mOrEventListener.enable();
	}


	private Size getBestSupportedSize(List<Size> sizes) {
		Size largestSize = sizes.get(0);
		int largestArea = sizes.get(0).height * sizes.get(0).width;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				largestArea = area;
				largestSize = s;
			}
		}
		return largestSize;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode)
			return true;
		return super.onKeyDown(keyCode, event);
	}
}
