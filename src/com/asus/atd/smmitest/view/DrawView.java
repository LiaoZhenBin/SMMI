package com.asus.atd.smmitest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huangzhijian on 2016/1/18.
 */
public class DrawView extends View {
    private int XPoint = 100;
    private int YPoint = 160;
    private int XScale = 10;  //刻度长
    private int XLength = 582;
    private int YLength = 150;
    private int MaxDataSize = 60;
    private static int memoryUsageRate;

    private List<Integer> data = new ArrayList<Integer>();

    /**
     * 用handler来实时更新坐标图
     */
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x1234){
                DrawView.this.invalidate();
            }
        };
    };

    /**
     * 构造方法
     * @param context
     */
    public DrawView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 将数据保存到data中
     */
    public void init(){
        WindowManager windowManager=(WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width=windowManager.getDefaultDisplay().getWidth();
        int height=windowManager.getDefaultDisplay().getHeight();
        XLength=width-150;
        YLength=height/6-40;
        YPoint=height/6-20;
        XScale=XLength/60;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(data.size() >= MaxDataSize){
                        data.remove(0);
                    }
                    data.add(memoryUsageRate);
                    handler.sendEmptyMessage(0x1234);
                }
            }
        }).start();}

    /**
     * 绘制坐标轴
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint minpaint = new Paint();
        minpaint.setStyle(Paint.Style.STROKE);
        minpaint.setAntiAlias(true); //去锯齿
        minpaint.setColor(Color.GRAY);
        minpaint.setStrokeWidth((float) 0.3);

        Paint maxpaint = new Paint();
        maxpaint.setStyle(Paint.Style.STROKE);
        maxpaint.setAntiAlias(true); //去锯齿
        maxpaint.setColor(Color.GRAY);
        maxpaint.setStrokeWidth((float) 0.6);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); //去锯齿
        paint.setARGB(255, 31, 97, 178);
        paint.setStrokeWidth((float) 3.0);

        Paint textpaint = new Paint();
        textpaint.setStyle(Paint.Style.STROKE);
        textpaint.setAntiAlias(true); //去锯齿
        textpaint.setColor(Color.WHITE);
        textpaint.setTextSize((float) 30);

        //画Y轴
        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, maxpaint);
        for(int j=1;j<=12;j++){
            if(j%4==0){
                canvas.drawLine(XPoint+XLength/12*j, YPoint - YLength, XPoint+XLength/12*j, YPoint, maxpaint);
            }
            canvas.drawLine(XPoint+XLength/12*j, YPoint - YLength, XPoint+XLength/12*j, YPoint, minpaint);
        }


        for(int i=0; i <3; i++) {
            canvas.drawText(50 * i + "%", XPoint -50-i*18, YPoint - i*YLength/2+15, textpaint);//文字
        }
        //画X轴
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, maxpaint);
        for(int k=1;k<=4;k++){
            if(k%4==0){
                canvas.drawLine(XPoint,YPoint-YLength/4*k,XPoint + XLength,YPoint-YLength/4*k,maxpaint);
            }
            canvas.drawLine(XPoint,YPoint-YLength/4*k,XPoint + XLength,YPoint-YLength/4*k,minpaint);
        }
        for(int l=0;l<4;l++){
            canvas.drawText(20*l+"s", XPoint+l*XLength/3-15, YPoint+40, textpaint);
        }

        if(data.size() > 1){
            for(int i=1; i<data.size(); i++){
                canvas.drawLine(XPoint + (data.size()-i) * XScale, YPoint - data.get(i-1)*3/2,
                        XPoint + (data.size()-i-1) * XScale, YPoint - data.get(i)*3/2, paint);
            }
        }
    }

    /**
     * 获取数据
     * @param data
     */
    public static void getData(int data){
        memoryUsageRate=data;
    }

}
