package com.asus.atd.smmitest.testcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.utils.SaveTestResult;
import com.asus.atd.smmitest.R;

import java.util.ArrayList;

/**
 * Created by huangzhijian on 2016/1/12.
 * huangzhijian@wind-mobi.com 20160113 mod begin
 * 实现多点触摸模块测试
 */
public class MultiTouchTesting extends Activity {

    private static final String mModuleName = "Multitouch Test";
    private static int mSucCount = 0;
    private static int mFailCount = 0;
    private ModuleManager mModuleManager;
    private PointerLocationView pointerLocationView;
    private boolean TESTOVER;
    private boolean isFinished = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = new RelativeLayout(this);
         pointerLocationView = new PointerLocationView(this);
        final Button btn = new Button(this);
        btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
        btn.setPadding(10,10, 10, 10);
        btn.setText(R.string.text_multitouch_fail);
        RelativeLayout.LayoutParams buttonLayout = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayout.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        relativeLayout.addView(btn, buttonLayout);
        relativeLayout.addView(pointerLocationView);
        setContentView(relativeLayout);
        this.getActionBar().hide();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFailCount++;
                setTestResult(Constants.TEST_FAILED, mFailCount);
                finishActivity(Constants.TEST_FAILED);
                setResult(Constants.TEST_FAILED);
            }
        });
        setTitle(mModuleName);
        TESTOVER=false;
    }

    /**
     * PointerState
     * create by huangzhijian, huangzhijian@wind-mobi.com
     * 定义接下来的圆圈绘制所需要的变量
     *
     */

    public class PointerState {
        private final ArrayList<Float> mXs = new ArrayList<Float>();
        private final ArrayList<Float> mYs = new ArrayList<Float>();
        private boolean mCurDown;
        private int mCurX;
        private int mCurY;
        private float mCurPressure;
        private float mCurSize;
        private int mCurWidth;
        private VelocityTracker mVelocity;
    }

    /**
     * PointerLocationView
     * create by huangzhijian, huangzhijian@wind-mobi.com
     * 绘制主要的布局界面和屏幕点击事件处理
     */
    public class PointerLocationView extends View {
        private final ViewConfiguration mVC;
        private final Paint mTextPaint;
        private final Paint mPaint;
        private boolean mCurDown;
        private int mCurNumPointers;
        private int mMaxNumPointers;
        private final ArrayList<PointerState> mPointers
                = new ArrayList<PointerState>();
        private boolean mPrintCoords = true;

        /**
         *构造方法
         * @param c 一个Context对象
         */
        public PointerLocationView(Context c) {
            super(c);
            setFocusable(true);
            mVC = ViewConfiguration.get(c);
            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextSize(25
                    * getResources().getDisplayMetrics().density);
            mTextPaint.setARGB(255, 255, 255, 255);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setARGB(255, 255, 255, 255);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth((float) 10.0);
            PointerState ps = new PointerState();
            ps.mVelocity = VelocityTracker.obtain();
            mPointers.add(ps);
        }

        /**
         * 主界面的绘制
         * @param canvas 主界面画布
         */
        @Override
        protected void onDraw(Canvas canvas) {
            synchronized (mPointers) {
                final int w = getWidth();
                final int h = getHeight();
                final int itemWidth = w / 2 - 110;
                final int itemHeight = h / 4;
                final int NP = mPointers.size();

                canvas.drawColor(getResources().getColor(R.color.black1));
                if (NP > 0) {
                    canvas.drawText(mCurNumPointers + " / " + "5 " + "touch",
                            itemWidth, itemHeight, mTextPaint);
                }
                for (int p = 0; p < NP; p++) {
                    final PointerState ps = mPointers.get(p);
                    if (mCurDown && ps.mCurDown) {
                        int pressureLevel = (int) (ps.mCurPressure * 255);
                        mPaint.setARGB(255, pressureLevel, 128, 255 - pressureLevel);
                        canvas.drawCircle(ps.mCurX, ps.mCurY, 80, mPaint);
                    }
                }
            }
        }

        /**
         * 触摸事件监听处理
         * @param event 传递一个触摸事件event
         */
        public void addTouchEvent(MotionEvent event) {
            synchronized (mPointers) {
                int action = event.getAction();
                int NP = mPointers.size();

                if (NP >= 5&&!TESTOVER) {
                    mSucCount++;
                    Log.d("mSuc",mSucCount+"mSucCount");
                    finishActivity(Constants.TEST_PASS);
                    setTestResult(Constants.TEST_PASS, mSucCount);
                    setResult(Constants.TEST_PASS);
                }

                if (action == MotionEvent.ACTION_DOWN) {
                    for (int p = 0; p < NP; p++) {
                        final PointerState ps = mPointers.get(p);
                        ps.mXs.clear();
                        ps.mYs.clear();
                        ps.mVelocity = VelocityTracker.obtain();
                        ps.mCurDown = false;
                    }
                    mPointers.get(0).mCurDown = true;
                    mMaxNumPointers = 0;
                }

                if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                    final int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int id = event.getPointerId(index);
                    while (NP <= id) {
                        PointerState ps = new PointerState();
                        ps.mVelocity = VelocityTracker.obtain();
                        mPointers.add(ps);
                        NP++;
                    }
                    final PointerState ps = mPointers.get(id);
                    ps.mVelocity = VelocityTracker.obtain();
                    ps.mCurDown = true;
                }

                final int NI = event.getPointerCount();

                mCurDown = action != MotionEvent.ACTION_UP
                        && action != MotionEvent.ACTION_CANCEL;
                mCurNumPointers = mCurDown ? NI : 0;
                if (mMaxNumPointers < mCurNumPointers) {
                    mMaxNumPointers = mCurNumPointers;
                }

                for (int i = 0; i < NI; i++) {
                    final int id = event.getPointerId(i);
                    final PointerState ps = mPointers.get(id);
                    ps.mVelocity.addMovement(event);
                    ps.mVelocity.computeCurrentVelocity(1);
                    final int N = event.getHistorySize();
                    for (int j = 0; j < N; j++) {
                        ps.mXs.add(event.getHistoricalX(i, j));
                        ps.mYs.add(event.getHistoricalY(i, j));
                    }
                    ps.mXs.add(event.getX(i));
                    ps.mYs.add(event.getY(i));
                    ps.mCurX = (int) event.getX(i);
                    ps.mCurY = (int) event.getY(i);
                    ps.mCurPressure = event.getPressure(i);
                    ps.mCurSize = event.getSize(i);
                    ps.mCurWidth = (int) (ps.mCurSize * (getWidth() / 3));
                }

                if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                    final int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int id = event.getPointerId(index);
                    final PointerState ps = mPointers.get(id);
                    ps.mXs.add(Float.NaN);
                    ps.mYs.add(Float.NaN);
                    ps.mCurDown = false;
                    if (mPrintCoords) {
                        Log.i("Pointer", "Pointer " + (id + 1) + ": UP");
                    }
                }

                if (action == MotionEvent.ACTION_UP) {
                    for (int i = 0; i < NI; i++) {
                        final int id = event.getPointerId(i);
                        final PointerState ps = mPointers.get(id);
                        if (ps.mCurDown) {
                            ps.mCurDown = false;
                        }
                    }
                }

                postInvalidate();
            }
        }

        /**
         * 设置触摸事件处理
         * @param event 传递一个触摸事件
         * @return
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            addTouchEvent(event);
            return true;
        }
    }

    /**
     * 对activity结束的一个处理
     * @param resultCode  一个int型结果参数，0为成功，1为失败
     */
    public void finishActivity(int resultCode) {
        if (MainManager.getInstance(this).mIsAutoTest && !isFinished) {
            MainManager.getInstance(this).sendFinishBroadcast(resultCode, mModuleName);
            isFinished = true;
            finish();
        } else {
            SaveTestResult.getInstance(this).saveResult(resultCode, mModuleName);
            setResult(resultCode);
            TESTOVER=true;
            finish();
        }
    }

    /**
     * 设置测试返回信息
     *
     * @param code  一个int型结果参数，0为成功，1为失败
     * @param count  一个int型数量参数，表示成功或者失败的次数
     */
    public void setTestResult(int code, int count) {


        for (TestCase module : mModuleManager.mTestModule) {
            if (module.getEnName().equals("Multitouch Test")) {
                if (code == Constants.TEST_PASS) {
                    module.setSuccessCount(count);
                } else{
                    module.setFailCount(count);
                }
            }
        }
    }

    /**
     * 手动结束测试返回
     */
    @Override
    public void onBackPressed() {
        mFailCount++;
        SaveTestResult.getInstance(this).saveResult(Constants.TEST_FAILED, mModuleName);
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
