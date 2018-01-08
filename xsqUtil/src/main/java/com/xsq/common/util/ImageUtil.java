package com.xsq.common.util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.provider.MediaStore;

import com.xsq.common.core.XsqCommon;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/1/19.
 */
public class ImageUtil {

    public static enum ImageType {
        JPEG,
        PNG;
    }

    /**
     * 存储图片
     *
     * @param image      图片
     * @param saveFolder 图片目录
     * @param imageName  图片名
     * @param quality    质量
     * @return
     */
    public static boolean saveJpgImage(Bitmap image, String saveFolder, String imageName, int quality) {
        if (saveFolder == null || imageName == null)
            return false;

        File picFileDir = new File(saveFolder);
        if (!picFileDir.exists()) {
            picFileDir.mkdir();
        }
        return saveImage(image, saveFolder + File.separator + imageName + ".jpg", ImageType.JPEG, quality);
    }

    /**
     * 存储图片
     *
     * @param image      图片
     * @param saveFolder 图片目录
     * @param imageName  图片名
     * @return
     */
    public static boolean savePngImage(Bitmap image, String saveFolder, String imageName) {
        if (saveFolder == null || imageName == null)
            return false;

        File picFileDir = new File(saveFolder);
        if (!picFileDir.exists()) {
            picFileDir.mkdir();
        }
        return saveImage(image, saveFolder + File.separator + imageName + ".png", ImageType.PNG, 100);
    }

    /**
     * 存储图片
     *
     * @param image    图片
     * @param savePath 图片全路径地址
     * @param type     类型
     * @param quality  质量
     * @return
     */
    public static boolean saveImage(Bitmap image, String savePath, ImageType type, int quality) {
        if (StorageUtil.isSDCardEnable() && image != null) {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(savePath));
                if (quality > 100) {
                    quality = 100;
                } else if (quality < 0) {
                    quality = 0;
                }
                if (type == ImageType.PNG) {
                    image.compress(Bitmap.CompressFormat.PNG, quality, bos);
                } else {
                    image.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                }
                bos.flush();
                bos.close();
                return true;
            } catch (Exception e) {
                LogUtil.error("saveImage error.", e);
                return false;
            }
        }
        return false;
    }

    /**
     * scale image
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     * @param src
     * @param scaleWidth
     * @param scaleHeight
     * @return
     */
    public static Bitmap scaleImage(Bitmap src, float scaleWidth, float scaleHeight) {
        if (src == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /**
     * 圆bitmap
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 生成bitmap缩略图
     * @param bitmap
     * @param needRecycle 是否释放bitmap原图
     * @param newHeight 目标宽度
     * @param newWidth 目标高度
     * @return
     */
    public static Bitmap createBitmapThumbnail(Bitmap bitmap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (needRecycle)
            bitmap.recycle();
        return newBitMap;
    }

    /**
     * 压缩bitmp到目标大小（质量压缩）
     * @param bitmap
     * @param needRecycle
     * @param maxSize
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length  > maxSize) {
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bm = BitmapFactory.decodeStream(isBm, null, null);
        if(needRecycle) {
            bitmap.recycle();
        }
        bitmap = bm;
        return bitmap;
    }

    /**
     * 等比压缩（宽高等比缩放）
     * @param bitmap
     * @param needRecycle
     * @param targetWidth
     * @param targeHeight
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, int targetWidth, int targeHeight) {
        float sourceWidth = bitmap.getWidth();
        float sourceHeight = bitmap.getHeight();

        float scaleWidth = targetWidth / sourceWidth;
        float scaleHeight = targeHeight / sourceHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight); //长和宽放大缩小的比例
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        bitmap = bm;
        return bitmap;
    }

    /**
     * 旋转bitmap
     * @param bitmap
     * @param degress 旋转角度
     * @param needRecycle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress, boolean needRecycle) {
        Matrix m = new Matrix();
        m.postRotate(degress);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        if(needRecycle) {
            bitmap.recycle();
        }
        return bm;
    }

    /**
     * 获取最后一次拍照的图片
     * @return
     */
    public static String getLatestCameraPicture() {

        if ( !StorageUtil.isSDCardEnable()) {
            return null;
        }

        String[] projection = new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE };
        Cursor cursor = XsqCommon.getInstance().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {
            String path = cursor.getString(1);
            return path;
        }
        return null;
    }



}
