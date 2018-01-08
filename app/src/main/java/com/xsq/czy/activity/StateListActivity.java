package com.xsq.czy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Spinner;

import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.adapter.StateListAdapter;
import com.xsq.czy.beans.Device;
import com.xsq.czy.net.GetDeviceListPackage;
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
 * Created by Administrator on 2017/5/3.
 */
public class StateListActivity extends Activity {

    /**设备运行状态监控列表*/
    private MyListView listView;

    private StateListAdapter adapter;

    private List<Device> list = new ArrayList<Device>();

    /**设备编号选择菜单*/
    private Spinner number;

    /**作业区选择菜单*/
    private Spinner area;

    /**授权状态选择*/
    private Spinner state;

    private ImageView backBtn;
    private String key="0";
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_state);
        initView();
//        initData();
        GetDevNameList();
    }

    private void initView() {
        listView = (MyListView) findViewById(R.id.activity_state_myListview);
        backBtn = (ImageView) findViewById(R.id.activity_state_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

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
                    listView.removeAllViewsInLayout();
                    list= new ArrayList<Device>();
                    GetDevNameList();

                    break;
                default:
                    break;
            }
        }
    };
    private void initData() {
        for (int i = 0; i < 12; i++) {
            Device equipState = new Device();
            equipState.setDept("平台" + i + "号");
            equipState.setMemberName("操作员" + i);
            equipState.setName("TD-00" + i);
            list.add(equipState);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取设备运行状态监控列表
     */
    private void GetDevNameList() {
        String userId = SharedPreferencesUtils.getString(StateListActivity.this, Resource.USERID,null);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        HttpUtil.executeRequestForJsonResultEx("GetDeviceList", map,
                new HttpUtil.ResultEventJson<GetDeviceListPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(StateListActivity.this,"网络异常");
                    }

                    @Override
                    public void onUIRoutine(GetDeviceListPackage resultObj) {
                        if (resultObj.getResult() == 200) {
                            list = resultObj.getNameList();
                            if (list == null || list.isEmpty()) {
                                list = Collections.emptyList();
                            }
                            adapter = new StateListAdapter(StateListActivity.this,list);
                            listView.setAdapter(adapter);
                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(StateListActivity.this,"获取列表失败");
                            }else {
                                ToastUtil.show(StateListActivity.this,resultObj.getDescript());
                            }
                        }
                    }
                });
    }

}
