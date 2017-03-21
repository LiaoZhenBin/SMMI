package com.asus.atd.smmitest.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.asus.atd.smmitest.R;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by lizusheng on 2016/6/22.
 */
public class UploadTask extends AsyncTask<String, String, String> {

    private static final String TAG = UploadTask.class.getSimpleName();

    Activity mActivity;
    public UploadTask(Activity mActivity){
        this.mActivity = mActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        String str = "";
        try
        {
            HttpGet localHttpGet = new HttpGet(strings[0]);
            Log.d(TAG, "UploadString : " + strings[0]);
            HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpGet);
            StatusLine localStatusLine = localHttpResponse.getStatusLine();
            if (localStatusLine.getStatusCode() == 200)
            {
                ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                localHttpResponse.getEntity().writeTo(localByteArrayOutputStream);
                str = localByteArrayOutputStream.toString();
                StringBuilder localStringBuilder = new StringBuilder();
                localByteArrayOutputStream.close();
                Log.d(TAG, "data_response " + str);
                Log.d(TAG,"upload success");
            }
            else
            {
                localHttpResponse.getEntity().getContent().close();
                IOException localIOException2 = new IOException(localStatusLine.getReasonPhrase());
                throw localIOException2;
            }
        }
        catch (ClientProtocolException localClientProtocolException)
        {
           Log.d(TAG,"upload fail");
            str = "fail";
        }
        catch (IOException localIOException1)
        {
           Log.d(TAG,"upload fail");
            str = "fail";
        }
        return str;
    }

    @Override
    protected void onPostExecute(String res) {
        super.onPostExecute(res);
            Log.d(TAG,"data_result " + res);
            String str = "";
            if (res.equals(this.mActivity.getString(R.string.flag_upload_response_success))) {
                str = this.mActivity.getString(R.string.msg_upload_response_success);
                Log.d(TAG,"onPostExecute -- res == SUCCESS" );
            }else if (res.startsWith("FAIL")) {
                str = res;
                Log.d(TAG,"onPostExecute -- res == FAIL" );
            }else if (res.equals("fail")){
			 str = res;
                Log.d(TAG,"onPostExecute -- res == fail" );
            }

            Intent intent = new Intent(Constants.BROADCAST_UPLOAD_RESPONSE);
            intent.putExtra("strResult", str);
            this.mActivity.sendBroadcast(intent);
             Log.d(TAG, "onPostExecute -- Send Broadcast to MainActivity");
            return;

        }

}
