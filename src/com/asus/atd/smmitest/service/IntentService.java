package com.asus.atd.smmitest.service;

import android.content.Intent;

import com.asus.atd.smmitest.utils.*;

/**
 * Created by liaozhenbin on 2016/1/12.
 */
public class IntentService extends android.app.IntentService {
    private static final String TAG = "IntentService";

    public IntentService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "IntentAction:"+intent.getAction());
        switch (intent.getAction()){
            case Constants.ACTION_EMODE_INIT_RESULT_FILE:
                FileUtils.initTestResultFile(this);
                break;
            case Constants.ACTION_EMODE_UPDATE_RESULT_FILE:
                FileUtils.updateTestResultFile(this);
                break;
            default:
                break;
        }
    }
}
