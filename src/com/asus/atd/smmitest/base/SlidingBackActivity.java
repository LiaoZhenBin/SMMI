package com.asus.atd.smmitest.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.asus.atd.smmitest.view.SlidingBackLayout;
import com.asus.atd.smmitest.R;

/**
 *
 * @author lizusheng@wind-mobi.com 2015/12/25
 *
 */
public class SlidingBackActivity extends Activity {
	protected SlidingBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (SlidingBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base_sliding, null);
		layout.attachToActivity(this);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}


}
