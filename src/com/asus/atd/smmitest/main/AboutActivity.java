package com.asus.atd.smmitest.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.R;

/**
 * Created by liuyang on 2016/1/13.
 */
public class AboutActivity extends BaseActivity {
    private TextView tv_name;
    private TextView tv_versoin;
    private TextView tv_info1;
    private TextView tv_info2;
    private TextView tv_website;
    private String mtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mtype = Build.MODEL;//获得手机型号
        tv_name.setText(mtype);
        tv_versoin.setText(Build.VERSION.RELEASE);// Android版本
    }

    @Override
    protected void findViewById() {
        tv_name=(TextView) findViewById(R.id.about_name);
        tv_versoin=(TextView) findViewById(R.id.about_version);
        tv_info1=(TextView) findViewById(R.id.about_info_lin1);
        tv_info2=(TextView) findViewById(R.id.about_info_lin2);
        tv_website=(TextView) findViewById(R.id.about_website);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }

    @Override
    public void finishActivity() {
        super.finishActivity();
    }
}
