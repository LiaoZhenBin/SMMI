package com.asus.atd.smmitest.testcase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

/**
 * Created by heliguang on 2016/1/18.
 */
public class TouchScreenTesting extends Activity{
    protected String TAG = this.getClass().getSimpleName();
    private ImageView iv_canvas;
    private Paint paint;
    private Bitmap baseBitmap;
    private Canvas canvas;
    public static int mSucCount = 0;
    public static int mFailCount = 0;
    private static final String mModuleName = "Touch Test";
    private ModuleManager mModuleManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_testing_touch);
        mModuleManager = ModuleManager.getInstance(this);

    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
            //liaozhenbin@wind-mobi.com 20160721 add begin
            new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    .setTitle("Test Finished").setMessage("Test finished, Please make a judgement").setCancelable(false).setPositiveButton("PASS", new DialogInterface.OnClickListener() {
                //liaozhenbin@wind-mobi.com 20160721 add end
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mSucCount++;
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    finishActivity(Constants.TEST_PASS);
                }
            }).setNegativeButton("FAIL",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mFailCount++;
                    setTestResult(Constants.TEST_FAILED, mFailCount);
                    finishActivity(Constants.TEST_FAILED);
                }
            }).show();
        }
        return true;
    }

    public void setTestResult(int code,int count){

        for (TestCase module:mModuleManager.mTestModule){
            if (module.getEnName().equals("Touch Test")){
                if(code == Constants.TEST_PASS)
                    module.setSuccessCount(count);
                else
                    module.setFailCount(count);
            }
        }
    }

    public void finishActivity(int resultCode){
        if (MainManager.getInstance(this).mIsAutoTest){
            MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
            finish();
            Log.d(TAG, "TouchScreen finished ,send broadcast to run the next test module");
        }else {
            SaveTestResult.getInstance(this).saveResult(resultCode ,mModuleName);
            setResult(resultCode);
            finish();
        }
    }
}

class MyPaintView extends View {
    private Resources myResources;

    // 画笔，定义绘制属性
    private Paint myPaint;
    private Paint mBitmapPaint;

    // 绘制路径
    private Path myPath;

    // 画布及其底层位图
    private Bitmap myBitmap;
    private Canvas myCanvas;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    // 记录宽度和高度
    private int mWidth;
    private int mHeight;

    public MyPaintView(Context context)
    {
        super(context);
        initialize();
    }

    public MyPaintView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public MyPaintView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    /**
     * 初始化工作
     */
    private void initialize()
    {
        // Get a reference to our resource table.
        myResources = getResources();

        // 绘制自由曲线用的画笔
        myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(myResources.getColor(R.color.white));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(12);

        myPath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 背景颜色
        canvas.drawColor(getResources().getColor(R .color.black));

        // 如果不调用这个方法，绘制结束后画布将清空
        canvas.drawBitmap(myBitmap, 0, 0, mBitmapPaint);

        // 绘制路径
        canvas.drawPath(myPath, myPaint);

    }

    private void touch_start(float x, float y)
    {
        myPath.reset();
        myPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            myPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up()
    {
        myPath.lineTo(mX, mY);
        // commit the path to our offscreen
        // 如果少了这一句，笔触抬起时myPath重置，那么绘制的线将消失
        myCanvas.drawPath(myPath, myPaint);
        // kill this so we don't double draw
        myPath.reset();
    }

    /**
     * 清除整个图像
     */
    public void clear()
    {

        // 清除方法：将位图清除为白色
        myBitmap.eraseColor(myResources.getColor(R.color.black));

        // 两种清除方法都必须加上后面这两步：
        // 路径重置
        myPath.reset();
        // 刷新绘制
        invalidate();
    }

}