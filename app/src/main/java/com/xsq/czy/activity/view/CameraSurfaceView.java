package com.xsq.czy.activity.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;


public class CameraSurfaceView extends SurfaceView
		implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.AutoFocusCallback, SensorEventListener {

	private static final String TAG = "CameraSurfaceView";
	public static final String PICTURE_FILE = "tempface.jpg";

	private Context mContext;
	private SurfaceHolder holder;
	private Camera mCamera;

	private int mScreenWidth;
	private int mScreenHeight;

	private Handler mAutoFocusHandler;

	private boolean mPreviewing = true;
	private boolean mAutoFocus = true;
	private boolean mSurfaceCreated = false;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	ImageView mImgMuban;
	ImageView mImgAuxiliary;

	public CameraSurfaceView(Context context) {
		this(context, null);
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		getScreenMetrix(context);
		initView();
	}
	public void setImg(ImageView mImgMuban, ImageView mImgAuxiliary){
		this.mImgMuban = mImgMuban;
		this.mImgAuxiliary=mImgAuxiliary;
	}
	private void getScreenMetrix(Context context) {
		WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		WM.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
	}

	private void initView() {
		holder = getHolder();// 获得surfaceHolder引用
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置类型
		mAutoFocusHandler = new Handler();
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");

		boolean sensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		Log.i(TAG, "sensor:" + sensor);

		if (mCamera == null) {
			mSurfaceCreated = true;
			int CammeraIndex =FindFrontCamera(); // 默认调用前摄像头
			if (CammeraIndex == -1) {
				CammeraIndex = FindBackCamera();
			}

			mCamera = Camera.open(CammeraIndex); // 开启相机

			try {
				mCamera.setPreviewDisplay(holder);// 摄像头画面显示在Surface上
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged");
		// 个别手机需要先调用
		setCameraDisplayOrientation(getContext(), FindBackCamera(), mCamera);
		// 设置参数并开始预览
		setCameraParams(mCamera, mScreenWidth, mScreenHeight);
		mCamera.startPreview();
		setAutoFocus(true);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		mCamera.stopPreview();// 停止预览
		mCamera.release();// 释放相机资源
		mCamera = null;
		holder = null;
		mSurfaceCreated = false;
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAutoFocus(boolean success, Camera Camera) {
		if (success) {
			Log.i(TAG, "onAutoFocus success=" + success);
		}
	}

	// 拍照瞬间调用
	private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			Log.i(TAG, "shutter");
		}
	};

	// 获得没有压缩过的图片数据
	private Camera.PictureCallback raw = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera Camera) {
			Log.i(TAG, "raw");

		}
	};

	// 创建jpeg图片回调数据对象
	private Camera.PictureCallback jpeg = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera Camera) {
			BufferedOutputStream bos = null;

			Bitmap bm = null;
			try {
				// 获得图片
				bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				Matrix m = new Matrix();

				if (android.os.Build.MODEL.equals("C68")) {
					Log.d("TAG", android.os.Build.MODEL);
					m.setRotate(90, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
				}else {
					m.setRotate(-90, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
					}
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					Log.i(TAG,
							"Environment.getExternalStorageDirectory()=" + Environment.getExternalStorageDirectory());
					// String filePath = "/sdcard/tempface.jpg";//照片保存路径
					File file = new File(Environment.getExternalStorageDirectory() + "/" + PICTURE_FILE);
					if (!file.exists()) {
						file.createNewFile();
					}
					bos = new BufferedOutputStream(new FileOutputStream(file));
					bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
				} else {
					Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
				}

				Intent intent = new Intent();

				((Activity) mContext).setResult(0xffffffff, intent);

				((Activity) mContext).finish(); // 结束当前的activity的生命周期

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bos.flush();// 输出
					bos.close();// 关闭
					bm.recycle();// 回收bitmap空间
					mCamera.stopPreview();// 关闭预览
					// mCamera.startPreview();// 开启预览
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	};

	public Camera getCamera() {
		return mCamera;
	}

	public void takePicture() {
		// 设置参数,并拍照
		setCameraParams(mCamera, mScreenWidth, mScreenHeight);
		// 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
		mCamera.takePicture(null, null, jpeg);
	}

	private void setCameraParams(Camera camera, int width, int height) {
		try {

			mPreviewing = true;

			int PreviewWidth = 0;
			int PreviewHeight = 0;
			Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
			Camera.Parameters parameters = mCamera.getParameters();

			// 获取摄像头支持的PictureSize列表
			List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
			for (Camera.Size size : pictureSizeList) {
				Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
			}
			// 获取摄像头支持的PreviewSize列表
			List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
			for (Camera.Size size : previewSizeList) {
				Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
			}

			Camera.Size preSize = MyCamPara.getInstance().getPreviewSize(previewSizeList, 2500);
			Camera.Size picSize = MyCamPara.getInstance().getPictureSize(pictureSizeList, 2500);
			Log.d(TAG, "previewSize and pictureSize" + picSize.width + "   " + picSize.height);

			// if (null == picSize) {
			// Log.i(TAG, "null == picSize");
			// picSize = parameters.getPictureSize();
			// }

			PreviewWidth = preSize.width;
			PreviewHeight = preSize.height;
			parameters.setPreviewSize(preSize.width, preSize.height); // 获得摄像区域的大小
			parameters.setPictureSize(picSize.width, picSize.height);// 设置拍出来的屏幕大小
			Log.i(TAG, "pre width=" + preSize.width + "  height=" + preSize.height);
			Log.i(TAG, "pic width=" + picSize.width + "  height=" + picSize.height);


			// parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片输出的格式
			//
			mCamera.setParameters(parameters);// 把上面的设置 赋给摄像头

			parameters.setJpegQuality(100); // 设置照片质量

			if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				Log.i("ddd", "FOCUS_MODE_AUTO");
			} else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); // 连续对焦模式
				Log.i("ddd", "FOCUS_MODE_CONTINUOUS_PICTURE");
			}

			int CammeraIndex =FindFrontCamera (); // 默认调用后置摄像头
			if (CammeraIndex == -1) {
				CammeraIndex = FindBackCamera();
			}

			// mCamera.cancelAutoFocus();// 自动对焦。
			// setAutoFocus(true);
			// 使照片和显示出来的角度一致
			// setCameraDisplayOrientation(getContext(), CammeraIndex, camera);
			Log.d("TAG", android.os.Build.MODEL);
			Log.d("TAGCammeraIndex", CammeraIndex+"");
			Log.d("TAG()",Camera.CameraInfo.CAMERA_FACING_FRONT+"");
			if ( CammeraIndex==Camera.CameraInfo.CAMERA_FACING_FRONT&&android.os.Build.MODEL.equals("C68")) {
				Log.d("TAG", android.os.Build.MODEL);
				mCamera.setDisplayOrientation(270);
			}else {
				mCamera.setDisplayOrientation(90);
			}

			// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
			mCamera.setParameters(parameters);

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

	}

	private int FindFrontCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				return camIdx;
			}
		}
		return -1;
	}

	private int FindBackCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				return camIdx;
			}
		}
		return -1;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
	
	}

	/**
	 * @param bmp
	 *            要旋转的图片
	 * @param degree
	 *            图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
	 * @return 旋转好的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		// 此处bitmap默认为RGBA_8888
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	public static void setCameraDisplayOrientation(Context content, int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = ((Activity) content).getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else {
			// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		camera.setDisplayOrientation(result);
	}

	public void setTorch(boolean newSetting) {
		Camera.Parameters parameters = mCamera.getParameters();
		doSetTorch(parameters, newSetting, false);
		mCamera.setParameters(parameters);
		// boolean currentSetting =
		// prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false);
		boolean currentSetting = false;
		if (currentSetting != newSetting) {
			// SharedPreferences.Editor editor = prefs.edit();
			// editor.putBoolean(PreferencesActivity.KEY_FRONT_LIGHT,
			// newSetting);
			// editor.commit();
		}
	}

	private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
		String flashMode;
		if (newSetting) {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_TORCH,
					Camera.Parameters.FLASH_MODE_ON);
		} else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_OFF);
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}

		/*
		 * SharedPreferences prefs =
		 * PreferenceManager.getDefaultSharedPreferences(context); if
		 * (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, false))
		 * { if (!safeMode) { ExposureInterface exposure = new
		 * ExposureManager().build(); exposure.setExposure(parameters,
		 * newSetting); } }
		 */
	}

	private static String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
		Log.i(TAG, "Supported values: " + supportedValues);
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(TAG, "Settable value: " + result);
		return result;
	}

	public void safeAutoFocus() {
		try {
			mCamera.autoFocus(autoFocusCB);

		} catch (RuntimeException re) {
			// Horrible hack to deal with autofocus errors on Sony devices
			// See https://github.com/dm77/barcodescanner/issues/7 for example
			scheduleAutoFocus(); // wait 1 sec and then do check again

		}
	}

	private void scheduleAutoFocus() {
		mAutoFocusHandler.postDelayed(doAutoFocus, 1000);

	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {

			if (mPreviewing && mAutoFocus && mSurfaceCreated) {
				safeAutoFocus();
			}
		}
	};

	// Mimic continuous auto-focusing
	Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {

			// scheduleAutoFocus();
		}
	};

	public void setAutoFocus(boolean state) {
		if (mPreviewing) {
			if (state == mAutoFocus) {
				return;
			}
			mAutoFocus = state;
			if (mAutoFocus) {
				if (mSurfaceCreated) { // check if surface created before using
										// autofocus
					Log.v(TAG, "Starting autofocus");
					safeAutoFocus();
				} else {
					scheduleAutoFocus(); // wait 1 sec and then do check again
				}
			} else {
				Log.v(TAG, "Cancelling autofocus");
				mCamera.cancelAutoFocus();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	private boolean mIsAuto = false;

	private boolean mInitialized = false;
	private float mLastX = 0;
	private float mLastY = 0;
	private float mLastZ = 0;

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		mAutoFocus = true;

		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
		}

		float deltaX = Math.abs(mLastX - x);
		float deltaY = Math.abs(mLastY - y);
		float deltaZ = Math.abs(mLastZ - z);

		if (mAutoFocus && (deltaX > .5 || deltaY > .5 || deltaZ > .5)) {
			mAutoFocus = false;
			mIsAuto = true;
		} else {
			mAutoFocus = true;

			if (mIsAuto) {
				scheduleAutoFocus();
			}
			mIsAuto = false;

		}
		Log.i(TAG, "mAutoFocus" + mAutoFocus + "");
		mLastX = x;
		mLastY = y;
		mLastZ = z;
	}

}
