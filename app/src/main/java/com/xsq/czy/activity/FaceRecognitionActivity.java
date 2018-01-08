package com.xsq.czy.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xsq.common.core.xsqcomponent.IHttpComponent;
import com.xsq.common.core.xsqcomponent.model.HttpObject;
import com.xsq.common.util.HttpUtil;
import com.xsq.czy.R;
import com.xsq.czy.activity.view.CustomDialog;
import com.xsq.czy.activity.view.LoadingDialog;
import com.xsq.czy.net.LockPwdPackage;
import com.xsq.czy.network.NetworkApi;
import com.xsq.czy.util.BitmapCompressor;
import com.xsq.czy.util.Constant;
import com.xsq.czy.util.FileUtils;
import com.xsq.czy.util.IOFormat;
import com.xsq.czy.util.ImageUtil;
import com.xsq.czy.util.PictureUtils;
import com.xsq.czy.util.PreferenceUtil;
import com.xsq.czy.util.Resource;
import com.xsq.czy.util.SharedPreferencesUtils;
import com.xsq.czy.util.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FaceRecognitionActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "FaceRecognitionActivity";

    private ImageView photoImg;

    private Button photographBtn;
    /**设置按钮*/
    private ImageView setBtn;

    public static final String PICTURE_FILE = "tempface.jpg";

    String bmpInfo;

    private Uri imageUri;

    private File userIconPathTemp;

    private StringBuffer pathSB;

    private static final String USER_ICON = "usericon.png"; // 用户头像截图照片名称

    private AlertDialog dialog;

    private static int count = 1;

    public static FaceRecognitionActivity faceActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_face);
        faceActivityContext = this;
        Constant.setContext(getApplicationContext());
        initView();
    }

    private void  initView() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        photoImg = (ImageView) findViewById(R.id.activity_face_photo);
        photographBtn = (Button) findViewById(R.id.activity_face_btn);
        setBtn = (ImageView) findViewById(R.id.activity_home_set_btn);
        setBtn.setOnClickListener(this);
        photographBtn.setOnClickListener(this);
        Log.i(TAG, "type: " + type);
        if (type.equals("0")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SetPwdDialog();
                }
            },3000);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == photographBtn) {
//            takePhoto2();
//            Intent intent = new Intent(getApplicationContext(),BlueYasuoActivity.class);
//            startActivity(intent);
         //  if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(FaceRecognitionActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
                }else {
                    openCamera2();
                }
           // }else {
              // openCamera();
          // }
        }else if (view == setBtn){
            Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera2();
               // Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               // startActivity(intent);
               // this.finish();
                ToastUtil.show(getApplicationContext(),"成功获取相机权限,开始进行人脸识别");
            }else {
                ToastUtil.show(getApplicationContext(),"您拒绝了相机权限,无法进行人脸识别");
            }
        }
    }

    protected void takePhoto2() {
        Intent intent = new Intent(FaceRecognitionActivity.this,TowerCameraActivity.class);
        startActivityForResult(intent,0);
    }

    protected void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PICTURE_FILE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        intent.putExtra("camerasensortype", 1); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        startActivityForResult(intent, 0);
    }

    private void openCamera2() {
        if (ContextCompat.checkSelfPermission(FaceRecognitionActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }
        //ntent intent = new Intent(getApplicationContext(), LoginActivity.class);
        //startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            loadingDialog = new LoadingDialog(FaceRecognitionActivity.this);
            loadingDialog.setMessage("正在识别中...").show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
           /* Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix,true);*/
            photoImg.setImageBitmap(bitmap);
            uploadUserIcon2(bitmap);
        }

        /*if (resultCode == RESULT_OK && requestCode == 0) {
            try {
                File f = new File(Environment.getExternalStorageDirectory() + "/" + PICTURE_FILE);
                ImageInfoModel model = new EXIFFile().GetIMGWidthAndHeigh(Environment.getExternalStorageState() + "/" + PICTURE_FILE);
                bmpInfo = "";
                bmpInfo += "原始图片，高：" + model.getImgHeight() + "宽:" + model.getImgWidth();
                if (f.exists()) {
                    Bitmap bm = ImageUtil.imageZoom(ImageUtil.scaleImage(f.getAbsolutePath(), 500f, 240f));
                    bmpInfo += "\r\n位图图片，高：" + bm.getHeight() + "宽:" + bm.getWidth();
//                    ((TextView) findViewById(R.id.txt_imginfo)).setText(bmpInfo);
                    photoImg.setImageBitmap(bm);
                    Distinguish(bm);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        /*printLogi("返回结果");
        printLogi("data = " + data);
        switch (requestCode) {
            case 2:
                printLogi("2 图片来自相机拍照");
                if (imageUri != null) {
                    cropImageUri(imageUri, 3);
                }
                break;
            case 3:
                printLogi("3 取得裁剪后的图片");
                if (imageUri != null) {
                    if(data != null){
                        setImageView(imageUri);
                    }
                }
                break;
        }*/

    }

    LoadingDialog loadingDialog = null;

    private void Distinguish(Bitmap photo) {
        loadingDialog = new LoadingDialog(FaceRecognitionActivity.this);
        loadingDialog.setMessage("正在识别中...").show();
        new GetTokenAsy().execute(PreferenceUtil.getName(FaceRecognitionActivity.this),ImageUtil.convertIconToString(photo),"识别测试");
    }

    class GetTokenAsy extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            return NetworkApi.InsertConstantDataForTowerCrane(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loadingDialog.cancel();
            try {
                System.out.println(s + "-");
                if (s.contains("SocketTimeoutException")) {
                    showDialog("服务器连接超时");
                    return;
                }
                switch (Integer.parseInt(new JSONObject(s).getString("result"))) {
                    case 0:
                        showDialog("开锁失败");// 识别失败
                        break;
                    case 1:
                        String data = new JSONObject(s).getString("data");
                        Intent intent = new Intent(FaceRecognitionActivity.this,BlueYasuoActivity.class);
                        Log.e("zsq",data);
                        intent.putExtra("data",data);
                        startActivity(intent);
                        finish();
                        break;
                    case -1:
                        showDialog("开锁失败");// 不是本人
                        break;
                    case 5:
                        showDialog("您账号已被冻结，请通知管理员进行开锁，" + new JSONObject(s).getString("msg") + "秒后可再次进行识别");
                        break;
                    default:
                        showDialog("开锁失败");// 识别失败
                        break;
                }
            }catch (Exception e) {
                showDialog("识别失败");
                e.printStackTrace();
            }
        }

    }

    public void showDialog(String text) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(text);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 设置你的操作事项
            }
        });
        builder.create().show();
    }

    /**
     * 打开照相机拍照
     */
    protected void openCamera(){
        printLogi("打开照相机拍照");
        imageUri = Uri.fromFile(createIconPathTemp());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //startActivityForResult(intent, 2);
        showDialog("您xxxxxx，" + imageUri);


                startActivityForResult(intent, 1);
    }

    private File createIconPathTemp(){
        userIconPathTemp = new File(Environment.getExternalStorageDirectory(),
                getUseiIconRelativePath(FileUtils.getFileName()));
        try {
            userIconPathTemp.createNewFile();
            printLogi("userIconPathTemp 创建成功");
            return userIconPathTemp;
        } catch (IOException e) {
            printLogi("头像文件创建失败 " + e.toString());
        }
        return null;
    }

    private String getUseiIconRelativePath(String userIconPath){
        pathSB = new StringBuffer();
        pathSB.append(Constant.TEMP_PATH);
        pathSB.append(File.separator);
        pathSB.append(Constant.getUserName());
        File tempPath = new File(Environment.getExternalStorageDirectory(), pathSB.toString());
        if (!tempPath.exists()) {
            tempPath.mkdirs();
        }
        pathSB.append(File.separator);
        pathSB.append(userIconPath);
        return pathSB.toString();
    }

    /**
     * 裁剪图片，数据直接绑定到URI中
     */
    private void cropImageUri(Uri uri, int requestCode){
        printLogi("裁剪图片");
        Intent intent = null;
        intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 设置截图宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);  // 设置宽高
        intent.putExtra("outputY", 800);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 裁剪图片2，数据从Data中返回
     */
    private void cropImageUri2(Uri uri){
        printLogi("裁剪图片2");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 设置截图宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // 设置宽高
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 5);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    @SuppressWarnings("deprecation")
    private void setImageView(Intent picdata){
        printLogi("设置图片到View");
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            photoImg.setImageBitmap(bitmap);
//			userIcon.setBackgroundDrawable(drawable);
            saveUserIcon(drawable);
            uploadUserIcon(drawable);
        }
    }

    /**
     * 本地保存用户图标
     * @param drawable
     */
    private void saveUserIcon(Drawable drawable){
        printLogi("缓存图标");
        if (drawable == null) {
            return;
        }
        String useiIconRelativePath = getUseiIconRelativePath(USER_ICON);
        FileUtils.copyFile(IOFormat.getInstance().drawable2InputStream(drawable), useiIconRelativePath); // 本地保存
        Constant.setUserIconPath(useiIconRelativePath);
        deleteTempIconPath();
    }

    // 删除头像临时文件
    private void deleteTempIconPath(){
        if (userIconPathTemp != null && userIconPathTemp.exists()) {
            userIconPathTemp.delete();
            printLogi("userIconPathTemp 删除成功");
            imageUri = null;
        } else {
            printLogi("userIconPathTemp 不存在");
        }
    }

    @SuppressWarnings("deprecation")
    private void setImageView(Uri imageUri){
        if(imageUri == null){
            return;
        }
        printLogi("设置图片到View");
        Bitmap bitmap = decodeUriAsBitmap(imageUri);
        if (bitmap != null) {
            int bitmapsize = FileUtils.getBitmapSize(bitmap);
            printLogi("bitmapsize = " + bitmapsize);
            if(bitmapsize == 0){
                return;
            }
            Drawable drawable = new BitmapDrawable(bitmap);
//			userIcon.setBackgroundDrawable(drawable);
            photoImg.setImageBitmap(bitmap);
            saveUserIcon(drawable);
            uploadUserIcon(drawable);
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            printLogi("解析Bitmap图片成功");
        } catch (FileNotFoundException e) {
            printLogi("找不到文件 " + e.toString());
        }
        return bitmap;
    }

    /**
     * 人脸识别
     */
    private void uploadUserIcon(Drawable drawable){

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap compressBitmap = BitmapCompressor.zoomImage(bitmap, 240, 240);
        InputStream is = PictureUtils.Bitmap2IS(compressBitmap);
        long lo = 0;
        lo = PictureUtils.len;

        String userId = SharedPreferencesUtils.getString(FaceRecognitionActivity.this, Resource.USERID,null);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        HttpUtil.executeStreamRequestEx("UploadUserImage", map, is, lo,
                new IHttpComponent.resultCallbackForInputStream() {
                    @Override
                    public void onSent(long sentSize, long sentTotal) {

                    }

                    @Override
                    public void onComplete(long sentSize) throws IOException {

                    }

                    @Override
                    public void onDataRoutine(HttpObject httpObject) {

                    }

                    @Override
                    public void onUIRoutine(HttpObject httpObject) {
                        Log.i(TAG, "人脸识别结果: " + httpObject.getResultCode());;
                        if (httpObject.getResultType() == HttpObject.ResultType.Success) {
                            if (httpObject.getResultCode() == 200) {
                                String result = httpObject.getResult().toString();
                                Log.i(TAG, "result: " + result);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String isPass = jsonObject.getString("isPass");
                                    String similar = jsonObject.getString("similar");
                                    if ("true".equals(isPass)) {
                                        Toast.makeText(FaceRecognitionActivity.this,"人脸识别成功", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(FaceRecognitionActivity.this,BlueYasuoActivity.class);
//                                        startActivity(intent);
                                        BlueYasuoActivity.actionStart(FaceRecognitionActivity.this,"0");
                                    }else {
                                        Toast.makeText(FaceRecognitionActivity.this,"人脸识别失败", Toast.LENGTH_SHORT).show();
                                        if (count >= 2) {
                                            InputPwdDialog();
                                        }else {
                                            count ++ ;
                                        }
                                    }
                                }catch (Exception e) {

                                }
                            }else{
                                Toast.makeText(FaceRecognitionActivity.this,"登陆信息过期！请重新登陆！", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        Log.i(TAG, "onExceptionRoutine: " + httpObject.getRequestType().toString());
                    }

                    @Override
                    public void onCancelRoutine(HttpObject httpObject) {

                    }
                });

    }

    /**
     * 上传头像到服务器
     */
    private void uploadUserIcon2(Bitmap bitmap){

//        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap compressBitmap = BitmapCompressor.zoomImage(bitmap, 240, 240);
        InputStream is = PictureUtils.Bitmap2IS(compressBitmap);
        long lo = 0;
        lo = PictureUtils.len;

        String userId = SharedPreferencesUtils.getString(FaceRecognitionActivity.this, Resource.USERID,null);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        HttpUtil.executeStreamRequestEx("UploadUserImage", map, is, lo,
                new IHttpComponent.resultCallbackForInputStream() {
                    @Override
                    public void onSent(long sentSize, long sentTotal) {

                    }

                    @Override
                    public void onComplete(long sentSize) throws IOException {

                    }

                    @Override
                    public void onDataRoutine(HttpObject httpObject) {

                    }

                    @Override
                    public void onUIRoutine(HttpObject httpObject) {
                        Log.i(TAG, "人脸识别结果: " + httpObject.getResultCode());;
                        if (httpObject.getResultType() == HttpObject.ResultType.Success) {
                            if (httpObject.getResultCode() == 200) {
                                loadingDialog.dismiss();
                                String result = httpObject.getResult().toString();
                                Log.i(TAG, "result: " + result);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String isPass = jsonObject.getString("isPass");
                                    String similar = jsonObject.getString("similar");
                                    if ("true".equals(isPass)) {
                                        Toast.makeText(FaceRecognitionActivity.this,"人脸识别成功", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(FaceRecognitionActivity.this,BlueYasuoActivity.class);
//                                        startActivity(intent);
                                        BlueYasuoActivity.actionStart(FaceRecognitionActivity.this,"0");
                                    }else {
                                        loadingDialog.dismiss();

                                        if (count >= 2) {
                                            Toast.makeText(FaceRecognitionActivity.this,"人脸识别失败，请输入密码", Toast.LENGTH_SHORT).show();
                                            InputPwdDialog();
                                        }else {
                                            Toast.makeText(FaceRecognitionActivity.this,"人脸识别失败，请重新拍照", Toast.LENGTH_SHORT).show();
                                            count ++ ;
                                        }
                                    }
                                }catch (Exception e) {

                                }
                            }

                        }
                    }

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        Log.i(TAG, "onExceptionRoutine: " + httpObject.getRequestType().toString());
                    }

                    @Override
                    public void onCancelRoutine(HttpObject httpObject) {

                    }
                });

    }

    /**
     * 密码输入弹出框
     */
    private void InputPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.input_pwd_dialog,null);
        builder.setView(view);
        final EditText pwdET = (EditText) view.findViewById(R.id.input_pwd_dialog_et);
        Button confirmBtn = (Button) view.findViewById(R.id.input_pwd_dialog_btn);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
//        dialog.setCancelable(false);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwdET.getText().toString().trim();
                if (password == null || "".equals(password)) {
                    pwdET.setError("请输入密码");
                }else {
//                    Intent intent = new Intent(FaceRecognitionActivity.this,BlueYasuoActivity.class);
//                    startActivity(intent);
                    CheckOperationPwd(password);
                }
            }
        });
    }

    /**
     * 4.2.5	操作密码（设备密码）
     * @param password 密码
     */
    private void CheckOperationPwd(String password) {
        String userId = SharedPreferencesUtils.getString(FaceRecognitionActivity.this,Resource.USERID,null);
        Map<String,Object> param = new HashMap<>();
        param.put("userId",userId);
        param.put("pwd",password);
        HttpUtil.executeRequestForJsonResultEx("CheckOperationPwd", param,
                new HttpUtil.ResultEventJson<LockPwdPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(getApplicationContext(),"网络异常");
                    }

                    @Override
                    public void onUIRoutine(LockPwdPackage resultObj) {
                        Log.i(TAG, "操作密码: " + resultObj.getResult());
                        if (resultObj.getResult() == 200) {
                            count = 1;
//                            Intent intent = new Intent(FaceRecognitionActivity.this,BlueYasuoActivity.class);
//                            startActivity(intent);
                            BlueYasuoActivity.actionStart(FaceRecognitionActivity.this,"1");
                            dialog.dismiss();
                        }else {
                            ToastUtil.show(getApplicationContext(),"输入密码错误");
                        }
                    }
                });
    }

    /**
     * 设置开锁密码
     */
    private void SetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.input_pwd_dialog,null);
        builder.setView(view);
        final EditText pwdET = (EditText) view.findViewById(R.id.input_pwd_dialog_et);
        Button confirmBtn = (Button) view.findViewById(R.id.input_pwd_dialog_btn);
        dialog = builder.show();
        dialog.setCancelable(false);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwdET.getText().toString().trim();
                if (password == null || "".equals(password)) {
                    pwdET.setError("请输入密码");
                }else {
//                    Intent intent = new Intent(FaceRecognitionActivity.this,BlueYasuoActivity.class);
//                    startActivity(intent);
                    LockPwd(password);
                }
            }
        });
    }

    /**
     * 4.3.4	设置开锁密码
     * @param password   密码
     */
    private void LockPwd(String password) {
        String userId = SharedPreferencesUtils.getString(FaceRecognitionActivity.this,Resource.USERID,null);
        Map<String,Object> param = new HashMap<>();
        param.put("userId",userId);
        param.put("pwd",password);
        HttpUtil.executeRequestForJsonResultEx("LockPwd", param,
                new HttpUtil.ResultEventJson<LockPwdPackage>() {

                    @Override
                    public void onExceptionRoutine(HttpObject httpObject) {
                        ToastUtil.show(getApplicationContext(),"网络异常");
                    }

                    @Override
                    public void onUIRoutine(LockPwdPackage resultObj) {
                        Log.i(TAG, "设置开锁密码结果: " + resultObj.getResult());
                        if (resultObj.getResult() == 200) {
                            ToastUtil.show(getApplicationContext(),"设置开锁密码成功");
                            dialog.dismiss();
                        }else {
                            ToastUtil.show(getApplicationContext(),"设置开锁密码失败");
                        }
                    }
                });
    }

    private void printLogi(String msg){
        Log.i("FaceRecognitionActivity", msg);
    }

    /**
     * 跳转页面
     * @param context
     * @param type 设备密码是否已经存在（0：不存在，1：存在）
     */
    public static void actionStart(Context context, String type) {
        Intent intent = new Intent(context,FaceRecognitionActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

}
