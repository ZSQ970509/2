package com.xsq.czy.activity.jpush;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xsq.czy.R;
import com.xsq.czy.beans.JPushBean;
import com.xsq.czy.util.Resource;

import cn.jpush.android.api.JPushInterface;

public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";

    private String title;

    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (null != intent) {
	        Bundle bundle = getIntent().getExtras();
            title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.i(TAG, "json数据: " + json);
//	        tv.setText("Title : " + title + "  " + "Content : " + content);
            Gson gson = new Gson();
            JPushBean jPushMsg = gson.fromJson(json,JPushBean.class);
            Log.i(TAG, "photoUrl: " + jPushMsg.getPhoto());
            Log.i(TAG, "originUrl: " + jPushMsg.getOrigin());
            Log.i(TAG, "msg: " + jPushMsg.getMsg());
            if (jPushMsg.getMsg().equals("2")) {
                setContentView(R.layout.activity_test);
                String photoUrl = Resource.URL + jPushMsg.getPhoto();
                String originUrl = Resource.URL + jPushMsg.getOrigin();
                ImageView photo = (ImageView) findViewById(R.id.activity_test_contrast_picture);   //现场照片
                ImageView origin = (ImageView) findViewById(R.id.activity_test_base_map);   //识别底图
                Glide.with(getApplicationContext()).load(photoUrl)
                        .error(R.drawable.load_error)
                        .into(photo);
                Glide.with(getApplicationContext()).load(originUrl)
                        .error(R.drawable.load_error)
                        .into(origin);
                return;
            }
        }
        TextView tv = new TextView(this);
        tv.setText("用户自定义打开的Activity");
        tv.setText("Title : " + title + "  " + "Content : " + content);
        addContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

}
