package com.xsq.czy.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.activity.jpush.ExampleUtil;
import com.xsq.czy.activity.jpush.LocalBroadcastManager;
import com.xsq.czy.adapter.ActionAdapter;
import com.xsq.czy.adapter.StateAdapter;
import com.xsq.czy.beans.Apply;
import com.xsq.czy.beans.Device;
import com.xsq.czy.net.GetApplyListPackage;
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

import cn.jpush.android.api.JPushInterface;

/**
 * 首页
 * Created by Administrator on 2017/5/2.
 */
public class HomeActivity extends Activity implements View.OnClickListener {

    /**设备运行状态监控列表*/
    private MyListView stateListView;
    public static final int DELAY = 10000;  //连击事件间隔
    private long lastClickTime = 0; //记录最后一次时间
    /**设备操作请求列表*/
    private MyListView actionListView;

    private StateAdapter stateAdapter;

    private ActionAdapter actionAdapter;

    private List<Device> stateList = new ArrayList<Device>();

    private List<Device> deviceList = new ArrayList<Device>();

    private List<Apply> actionList = new ArrayList<Apply>();

    private List<Apply> applyList = new ArrayList<Apply>();

    /**更多的设备运行状态监控列表*/
    private TextView stateMoreTv;

    /**更多的设备操作请求列表*/
    private TextView requestMoreTv;

    /**设置按钮*/
    private ImageView setBtn;

    public static Activity homeActivityContext;//自身的context
    public static boolean isForeground = false;
    private String key="0";
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        homeActivityContext = this;
//        stateList.clear();
//        deviceList .clear();
//        actionList.clear();
//        applyList .clear();
        initView();
        registerMessageReceiver();  // used for receive msg
//        init();
//        initData();
    //   GetDevNameList();
  //     GetApplyList();
    }

    private void initView() {
        stateMoreTv = (TextView) findViewById(R.id.activity_home_state_more);
        stateMoreTv.setOnClickListener(this);
        requestMoreTv = (TextView) findViewById(R.id.activity_home_request_more);
        requestMoreTv.setOnClickListener(this);
        setBtn = (ImageView) findViewById(R.id.activity_home_set_btn);
        setBtn.setOnClickListener(this);
        stateListView = (MyListView) findViewById(R.id.activity_home_state_listview);
        actionListView = (MyListView) findViewById(R.id.activity_home_action_listview);

        actionListView.setOnItemClickListener(new MyOnItemClickListener());


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
                    stateListView.removeAllViewsInLayout();
                    actionListView.removeAllViewsInLayout();

                    GetDevNameList();

                    GetApplyList();
                    break;
                default:
                    break;
            }
        }
    };
    private void initData() {
        for (int i = 0;i < 4; i++) {
            Device equipState = new Device();
            equipState.setDept("平台" + i + "号");
            equipState.setMemberName("操作员" + i);
            equipState.setName("TD-00" + i);
            stateList.add(equipState);
        }
        for (int i = 0; i < 4; i++) {
            Apply equipAction = new Apply();
            equipAction.setDeviceName("TD-00" + i);
            equipAction.setMemberName("操作员" + i);
            equipAction.setDept("平台" + i + "号");
            equipAction.setTime("2017-04-17 00:00:00");
            actionList.add(equipAction);
        }
        stateAdapter.notifyDataSetChanged();
        actionAdapter.notifyDataSetChanged();
    }

    /**
     * 获取设备运行状态监控列表
     */
    private void GetDevNameList() {
        stateList.clear();
        deviceList.clear();

        String userId = SharedPreferencesUtils.getString(HomeActivity.this, Resource.USERID,null);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        HttpUtil.executeRequestForJsonResultEx("GetDeviceList", map,
                new HttpUtil.ResultEventJson<GetDeviceListPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(HomeActivity.this,"网络异常");
                    }

                    @Override
                    public void onUIRoutine(GetDeviceListPackage resultObj) {
                        if (resultObj.getResult() == 200) {
                            stateList = resultObj.getNameList();
                            if (stateList == null || stateList.isEmpty()) {
                                stateList = Collections.emptyList();
                                return;
                            }
                            if (stateList.size() > 4) {
                                for (int i = 0; i < 4; i++) {
                                    deviceList.add(stateList.get(i));
                                }
                            }else {
                                deviceList = stateList;
                            }

                            stateAdapter = new StateAdapter(HomeActivity.this,deviceList);

                            stateListView.setAdapter(stateAdapter);
                            stateAdapter.notifyDataSetChanged();

                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(HomeActivity.this,"获取设备运行状态监控列表失败");
                            }else {
                                ToastUtil.show(HomeActivity.this,resultObj.getDescript());
                            }
                        }
                    }
                });
    }

    /**
     * 获取设备请求列表
     */
    private void GetApplyList() {
        actionList.clear();
        applyList.clear();
        String userId = SharedPreferencesUtils.getString(HomeActivity.this,Resource.USERID,null);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        HttpUtil.executeRequestForJsonResultEx("GetApplyList", map,
                new HttpUtil.ResultEventJson<GetApplyListPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(HomeActivity.this,"网络异常");
                    }

                    @Override
                    public void onUIRoutine(GetApplyListPackage resultObj) {
                        if (resultObj.getResult() == 200) {
                            actionList = resultObj.getApplyList();
                            if (actionList == null || actionList.isEmpty()) {
                                actionList = Collections.emptyList();
                                return;
                            }
                            Collections.reverse(actionList);
                            if (actionList.size() > 4) {
                                for (int i = 0; i < 4; i++) {
                                    applyList.add(actionList.get(i));
                                }
                            }else {
                                applyList = actionList;
                            }
                            actionAdapter = new ActionAdapter(HomeActivity.this,applyList);
                            actionListView.setAdapter(actionAdapter);
                            actionAdapter.notifyDataSetChanged();
                         //   ToastUtil.show(HomeActivity.this,"惺惺惜惺惺");

                        }else {
                            if (resultObj.getDescript() == null || "".equals(resultObj.getDescript())) {
                                ToastUtil.show(HomeActivity.this,"获取设备请求列表失败");
                            }else {
                                ToastUtil.show(HomeActivity.this,resultObj.getDescript());
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        if (view == stateMoreTv) {
            Intent intent = new Intent(getApplicationContext(),StateListActivity.class);
            startActivity(intent);
        }else if (view == requestMoreTv) {
            Intent intent = new Intent(getApplicationContext(),RequestListActivity.class);
            startActivity(intent);
        }else if (view == setBtn) {
            Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
            startActivity(intent);
        }
    }

    private void init(){
        JPushInterface.init(getApplicationContext());
    }

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
//                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

//    private void setCostomMsg(String msg){
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
//    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




                String photo = Resource.URL + applyList.get(position).getPhoto();
                String origin = Resource.URL + applyList.get(position).getOrigin();

                ContrastPictureActivity.actionStart(HomeActivity.this, photo, origin);



//            Intent intent = new Intent(RequestListActivity.this,BlueLockActivity.class);
//            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        GetDevNameList();
        GetApplyList();

        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }
    protected void onStop() {
        actionListView.removeAllViewsInLayout();
        applyList.clear();


        actionAdapter = new ActionAdapter(HomeActivity.this,applyList);
        actionListView.setAdapter(actionAdapter);
        actionAdapter.notifyDataSetChanged();
        super.onStop();

    }
}
