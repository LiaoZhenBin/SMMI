package com.asus.atd.smmitest.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.base.CommonAdapter;
import com.asus.atd.smmitest.base.ViewHolder;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lizusheng on 2016/1/2.
 */
public class ResultActivity extends BaseActivity{
    private TextView mTvTitle;
    private ListView mListView;
    private CommonAdapter mCommonAdapter;
    private ModuleManager mModuleManager;
    public  ArrayList<TestCase> mTestCases =new ArrayList<TestCase>();
    private ArrayList <HashMap<String,Integer>> mApapterList;
    private int mPassCount = 0;
    private int mFailedCount = 0;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_result);
    }

    @Override
    protected void findViewById() {
        mTvTitle= (TextView) findViewById(R.id.tv_title_result);
        mListView= (ListView) findViewById(R.id.lv_result_test);
    }

    @Override
    protected void setListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    protected void init() {
        mModuleManager=ModuleManager.getInstance(this);
        if (!mModuleManager.mXmlParsed) {
            mModuleManager.parseXML();
        }
        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();
        mApapterList = (ArrayList<HashMap<String, Integer>>) mModuleManager.loadModuleResource(mTestCases);

        getResult();

        mCommonAdapter = new CommonAdapter<HashMap<String, Integer>>(this, mApapterList, R.layout.item_listview_result) {
                @Override
                public void convert(ViewHolder holder, HashMap<String, Integer> item) {
                    holder.setText(R.id.tv_lv_result, getString(item.get("Name")));
                    holder.setImageResource(R.id.icon_lv_result, item.get("Image"));
                    holder.setText(R.id.tv_passed_result, getString(R.string.tv_passed_result) + item.get("SuccessCount").toString());
                    holder.setText(R.id.tv_failed_result, getString(R.string.tv_failed_result) + item.get("FailCount").toString());
                    int resultCode = item.get("Result");
                    if (resultCode == Constants.TEST_PASS) {
                        holder.setImageResource(R.id.res_lv_result, R.drawable.test_pass);
                    }else if(resultCode == Constants.TEST_FAILED) {
                        holder.setImageResource(R.id.res_lv_result, R.drawable.test_fail);
                    }
                    else{
                        holder.setImageResource(R.id.res_lv_result, R.drawable.untest);
                    }
                }
            };
            mListView.setAdapter(mCommonAdapter);
            /*mTvTitle.setText("Pass:" + mPassCount + "    " + "Failed:" + mFailedCount +
                    "    " + "UnTest:" + (mApapterList.size() - (mPassCount + mFailedCount)));*/
        }

    /**
     * 获取测试结果
     */
    public void getResult(){
        SharedPreferences sp = getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_PRIVATE);
        String results = sp.getString(Constants.PREF_KEY_TEST_RESULTS_PHONE, "");
        boolean hasTested = false;
        if (!TextUtils.isEmpty(results)){
            String[] testModules = results.split(",");
            for (int i=0;i<mTestCases.size();i++){
                hasTested = false;
                for (int j=0;j<testModules.length;j++){
                    String[] testModult = testModules[j].split("=");
                    if (mTestCases.get(i).getEnName().equals(testModult[0])){
                        mApapterList.get(i).put("Result", Integer.parseInt(testModult[1]));
                        mApapterList.get(i).put("SuccessCount", mTestCases.get(i).getSuccessCount());
                        mApapterList.get(i).put("FailCount", mTestCases.get(i).getFailCount());
                        if(Integer.parseInt(testModult[1]) == 0){
                            mPassCount++;
                        }else{
                            mFailedCount++;
                        }
                        hasTested = true;
                        Log.d(TAG,"Module EnName:"+mTestCases.get(i).getEnName()+
                                "mPassCount:" + mPassCount + "mFailedCount" + mFailedCount);
                    }
                }
                if (!hasTested){
                    Log.d(TAG,"init sp module :"+mTestCases.get(i).getEnName());
                    mApapterList.get(i).put("Result", -1);
                    mApapterList.get(i).put("SuccessCount", 0);
                    mApapterList.get(i).put("FailCount", 0);
                }

            }
        }else {
            for (int i=0;i<mTestCases.size();i++){
                mApapterList.get(i).put("Result", -1);
                mApapterList.get(i).put("SuccessCount", 0);
                mApapterList.get(i).put("FailCount", 0);
                mPassCount = 0;
                mFailedCount = 0;
            }
            Log.d(TAG,"Has not result sp");
         //   return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }


}
