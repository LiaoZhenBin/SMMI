package com.asus.atd.smmitest.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.asus.atd.smmitest.R;

public abstract class BaseActivity extends SlidingBackActivity implements IModel , View.OnClickListener{
    protected String TAG = this.getClass().getSimpleName();
    protected static final boolean DBG = true;

    protected static final String TAG_PASS = "1";
    protected Button mBtnTest;
/*    public static int mSucCount = 0;
    public static int mFailCount = 0;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        View decorView = this.getWindow().getDecorView();
        if (findViewById(R.id.btn_bar) == null) {
            View v = LayoutInflater.from(this).inflate(R.layout.base_bar_btn, (ViewGroup) decorView);
            mBtnTest = (Button) v.findViewById(R.id.btn_start);
            mBtnTest.setOnClickListener(this);
            mBtnTest.setTag(TAG_PASS);

      /*  RelativeLayout.LayoutParams relLayoutParams=new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            this.addContentView(v,relLayoutParams);*/
        }
        setContentView();
        findViewById();
        init();
        setListener();
    }
    protected abstract void setContentView();
    protected abstract void findViewById();
    protected abstract void setListener();
    protected abstract void init();

    @Override
    public void startTest() {

    }

    @Override
    public void reStartTest() {

    }

    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    protected void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finishActivity();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishActivity(){
        finish();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                startTest();
                break;
            default:
                break;
        }
    }

    private OnStartListener listener;

    public interface OnStartListener {
        void onStarting();
        void onStarted();
    }

    public void setOnStartListener(OnStartListener listener){
        this.listener=listener;
    }
}


