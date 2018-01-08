package com.xsq.czy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.adapter.OldActionAdapter;
import com.xsq.czy.beans.Apply;
import com.xsq.czy.net.GetApplyListPackage;
import com.xsq.czy.util.MyListView;
import com.xsq.czy.util.Resource;
import com.xsq.czy.util.SharedPreferencesUtils;
import com.xsq.czy.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/2.
 */
public class RequestListActivity extends Activity {

    /**设备操作请求列表*/
    private MyListView listView;
    private List<Apply> applyList = new ArrayList<Apply>();
    private List<Apply> applyList1 = new ArrayList<Apply>();
    private OldActionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    /**设备编号下拉菜单*/
    private Spinner number;

    /**作业区下拉菜单*/
    private Spinner area;

    /**授权状态下拉菜单*/
    private Spinner state;

    private List<Apply> list = new ArrayList<Apply>();
    private List<Apply> list1 = new ArrayList<Apply>();
    private ImageView backBtn;
    private String key="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_request);
        initView();
        initListener();
    //  initData();
   GetApplyList();
    }

    private void initView() {
        listView = (MyListView) findViewById(R.id.activity_request_listview);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        backBtn = (ImageView) findViewById(R.id.activity_request_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        number = (Spinner) findViewById(R.id.activity_request_number_spinner);
        area = (Spinner) findViewById(R.id.activity_request_area_spinner);
        state = (Spinner) findViewById(R.id.activity_request_state_spinner);

        /**
         * 下拉刷新
         */
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.lavenderblush);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.white);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
    }
    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    key="1";
                    swipeRefreshLayout.setRefreshing(false);

                    list = new ArrayList<Apply>();
                    applyList = new ArrayList<Apply>();
                    listView.removeAllViewsInLayout();
                    GetApplyList();
                    break;
                default:
                    break;
            }
        }
    };
    private void initListener() {
        number.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String photo = Resource.URL + list.get(position).getPhoto();
            String origin = Resource.URL + list.get(position).getOrigin();
            ContrastPictureActivity.actionStart(RequestListActivity.this, photo, origin);
//            Intent intent = new Intent(RequestListActivity.this,BlueLockActivity.class);
//            startActivity(intent);
        }
    }

    private void initData() {

        for (int i = 0; i < 12; i++) {
            Apply equipAction = new Apply();
            equipAction.setDeviceName("TD-00" + i);
            equipAction.setMemberName("操作员" + i);
            equipAction.setDept("平台" + i + "号");
            equipAction.setTime("2017-04-17 00:00:00");
            list.add(equipAction);
            applyList.add(list.get(i));
        }
        //adapter.notifyDataSetChanged();
        adapter = new OldActionAdapter(getApplicationContext(),applyList);
        listView.setAdapter(adapter);
    }

    /**
     * 获取设备请求列表
     */
    private void GetApplyList() {
        String userId = SharedPreferencesUtils.getString(RequestListActivity.this, Resource.USERID,null);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        HttpUtil.executeRequestForJsonResultEx("GetApplyList", map,
                new HttpUtil.ResultEventJson<GetApplyListPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(RequestListActivity.this,"网络异常");
                    }

                    @Override
                    public void onUIRoutine(GetApplyListPackage resultObj) {
                        if (resultObj.getResult() == 200) {
                            list = resultObj.getApplyList();
                            if (list == null || list.isEmpty()) {
                                list = Collections.emptyList();
                            }
                            Collections.reverse(list);
                            for (int i = 0; i < list.size(); i++) {

                                applyList.add(list.get(i));
                            }

                            adapter = new OldActionAdapter(getApplicationContext(),applyList);
                            listView.setAdapter(adapter);
                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(RequestListActivity.this,"获取列表失败");
                            }else {
                                ToastUtil.show(RequestListActivity.this,resultObj.getDescript());
                            }
                        }
                    }
                });
    }

}
