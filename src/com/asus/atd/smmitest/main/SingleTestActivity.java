package com.asus.atd.smmitest.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.asus.atd.smmitest.base.BaseActivity;
import com.asus.atd.smmitest.base.CommonAdapter;
import com.asus.atd.smmitest.base.ViewHolder;
import com.asus.atd.smmitest.data.TestCase;
import com.asus.atd.smmitest.manager.MainManager;
import com.asus.atd.smmitest.manager.ModuleManager;
import com.asus.atd.smmitest.testcase.CommonTestActivity;
import com.asus.atd.smmitest.utils.Constants;
import com.asus.atd.smmitest.utils.Log;
import com.asus.atd.smmitest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class SingleTestActivity extends BaseActivity {
    private ListView listView;
    private ArrayList<HashMap<String, Integer>> mApapterList;
    private CommonAdapter<HashMap<String, Integer>> mCommonAdapter;
    private MainManager mMainManager;
    private ModuleManager mModuleManager;
    public static ArrayList<TestCase> mTestCases = new ArrayList<TestCase>();
    public static ArrayList<HashMap<String, Integer>> mCurrentResult = new ArrayList<HashMap<String, Integer>>();
    private int mTestResult = -1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_singletest);
        setTitleColor(R.color.silver);
        mMainManager = MainManager.getInstance(this);
        mModuleManager = mModuleManager.getInstance(this);
    }

    @Override
    protected void findViewById() {
        listView = (ListView) findViewById(R.id.lv_single_test);
    }

    @Override
    protected void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // mCurrentResult.add(i);
                boolean mHasTest = false;
                HashMap<String, Integer> mResultMap;
                CommonAdapter mAdapter = (CommonAdapter) adapterView.getAdapter();
                String mModuleName = getString((Integer) ((HashMap) mAdapter.getItem(i)).get("Name"));
                for (TestCase module : mTestCases) {
                    if (module.getEnName().equals(mModuleName)) {
                        Intent intent = new Intent(SingleTestActivity.this, CommonTestActivity.class);
                        intent.putExtra("ModuleImage", mModuleManager.mTestModuleImage.get(module.getEnName()));
                        intent.putExtra("ModuleName", module.getEnName());
                        intent.putExtra("ModuleActivity", module.getActivity());
                        // gaohui@wind-mobi.com 2016/03/17 add start
                        String moduleStr = "";
                        if (mModuleManager.mTestModuleStr.get(module.getEnName()) != null) {
                            moduleStr = getString(mModuleManager.mTestModuleStr.get(module.getEnName()));
                        }
                        intent.putExtra("ModuleStr", moduleStr);
                        // gaohui@wind-mobi.com 2016/03/17 add end
                        startActivityForResult(intent, 2);
                    }
                }
                for (int j = 0; j < mCurrentResult.size(); j++) {
                    if (mCurrentResult.size() > 0) {
                        HashMap map = (HashMap) mCurrentResult.get(j);
                        Iterator it = map.keySet().iterator();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            if (key.equals(mModuleName)) {
                                mHasTest = true;
                            }
                        }
                    }
                }
                if (!mHasTest) {
                    mResultMap = new HashMap<String, Integer>();
                    mResultMap.put(mModuleName, -1);
                    mCurrentResult.add(mResultMap);
                }
            }
        });
    }

    /**
     * 加载所有测试单元
     */
    @Override
    protected void init() {

        if (!mModuleManager.mXmlParsed) {
            if (DBG) Log.d(TAG, "the mXmlParse is false , start to parse the Xml");
            mModuleManager.parseXML();
        }
        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();
        mApapterList = (ArrayList<HashMap<String, Integer>>) mModuleManager.loadModuleResource(mTestCases);
        mCommonAdapter = new CommonAdapter<HashMap<String, Integer>>(this, mApapterList, R.layout.item_listview_singletest) {
            @Override
            public void convert(ViewHolder holder, HashMap<String, Integer> item) {
                holder.setText(R.id.tv_lv_single_test, getString(item.get("Name")));
                holder.setImageResource(R.id.iv_lv_single_test, item.get("Image"));

                if (mCurrentResult.size() != 0) {
                    for (int j = 0; j < mCurrentResult.size(); j++) {
                        HashMap map = (HashMap) mCurrentResult.get(j);
                        Iterator it = map.keySet().iterator();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            if (key.equals(getString(item.get("Name")))) {
                                if ((Integer) map.get(key) == Constants.TEST_PASS) {
                                    holder.setImageResource(R.id.iv_result_test, R.drawable.asus_diagnostic_ic_pass2);
                                } else if ((Integer) map.get(key) == Constants.TEST_FAILED) {
                                    holder.setImageResource(R.id.iv_result_test, R.drawable.asus_diagnostic_ic_fail2);
                                }
                            }
                        }
                    }
                }
            }
        };
        listView.setAdapter(mCommonAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_singletest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_test_result:
                startActivity(new Intent(SingleTestActivity.this, ResultActivity.class));
                break;
            case R.id.action_clear_cache:
                // liaozhenbin@wind-mobi.com 2016/01/29 add start
                showResetDialog();
                // liaozhenbin@wind-mobi.com 2016/01/29 add end
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // liaozhenbin@wind-mobi.com 2016/01/29 add start
    private void showResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Reset");
        builder.setMessage("Tap reset ro clear all of the test result?");

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("emode_phone_results", "");
                editor.commit();
                // liaozhenbin@wind-mobi.com 2016/07/02 add start
                mCurrentResult.clear();
//                init();
                mCommonAdapter.notifyDataSetChanged();

                // liaozhenbin@wind-mobi.com 2016/01/02 add end
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // liaozhenbin@wind-mobi.com 2016/01/29 add end

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO
        mTestResult = resultCode;
        if (data == null) {
            Log.w(TAG, "onActivityResult, intent is null");
            return;
        }
        String name = data.getStringExtra("ModuleName");
        for (int j = 0; j < mCurrentResult.size(); j++) {
            HashMap map = (HashMap) mCurrentResult.get(j);
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.equals(name)) {
                    map.put(key, resultCode);
                }
            }
        }
        init();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentResult.clear();
    }
}
