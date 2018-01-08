package com.xsq.czy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xsq.czy.R;

/**
 * Created by Administrator on 2017/5/21.
 */
public class ContrastPictureActivity extends Activity {

    private static final String TAG = "ContrastPictureActivity";

    private ImageView photo;

    private ImageView origin;

    private ImageView backBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        String photoUrl = intent.getStringExtra("photo");
        String originUrl = intent.getStringExtra("origin");
        Log.i(TAG, "photoUrl: " + photoUrl);
        Log.i(TAG, "originUrl: " + originUrl);
        photo = (ImageView) findViewById(R.id.activity_test_contrast_picture);   //现场照片
        origin = (ImageView) findViewById(R.id.activity_test_base_map);   //识别底图
        backBtn = (ImageView) findViewById(R.id.activity_test_back_btn);

        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(getApplicationContext()).load(photoUrl)
                .error(R.drawable.load_error)
                .into(photo);
        Glide.with(getApplicationContext()).load(originUrl)
                .error(R.drawable.load_error)
                .into(origin);
    }

    /**
     * 页面跳转
     * @param context
     * @param photo    申请图片url
     * @param origin   底图url
     */
    public static void actionStart(Context context, String photo, String origin) {
        Intent intent = new Intent(context, ContrastPictureActivity.class);
        intent.putExtra("photo",photo);
        intent.putExtra("origin",origin);
        context.startActivity(intent);
    }

}
