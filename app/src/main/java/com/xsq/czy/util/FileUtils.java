package com.xsq.czy.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**文件操作工具类
 * Created by Administrator on 2017/4/24.
 */
public class FileUtils {

    private static String TAG = "FileUtils";
    private static StringBuffer tempPhoneFileName; // 保存照片的文件名称
    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/Photo_LJ/";

    /**
     * 复制文件
     *
     * @param copyFile 要复制的文件InputStream
     * @param savePath 要保存到哪里
     */
    public static void copyFile(InputStream is, File savePath){
        if(is == null && savePath == null){
            printLogi("复制文件时缺少参数 FileUtils.copyFile()");
            return;
        }
        Utils.printLogi(Utils.class.getSimpleName(), "开始复制文件");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            Utils.printLogi(TAG, "is.available() = " + is.available());
            Utils.printLogi(TAG, "savePath = " + savePath);
            in = new BufferedInputStream(is);
            if(!savePath.exists()){
                savePath.mkdirs();
            }
            out = new BufferedOutputStream(new FileOutputStream(savePath));
            byte[] bytes = new byte[1024];
            int by = 0;
            while ((by = in.read(bytes)) != -1) {
                out.write(bytes, 0, by);
            }
            Utils.printLogi(Utils.class.getSimpleName(), "成功复制文件");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIn(in, is);
            closeOut(out);
        }
    }

    /**
     * 复制文件
     *
     * @param copyFile 要复制的文件InputStream
     * @param savePath 要保存到哪里 String类型
     */
    public static void copyFile(InputStream is, String savePath){
        if(is == null && savePath == null){
            printLogi("复制文件时缺少参数 FileUtils.copyFile()");
            return;
        }
        Utils.printLogi(TAG, "开始复制文件");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            Utils.printLogi(TAG, "is.available() = " + is.available());
            Utils.printLogi(TAG, "savePath = " + savePath);
            in = new BufferedInputStream(is);
            File sP = new File(Environment.getExternalStorageDirectory(), savePath);
            long length = sP.length();
            printLogi("length = " + length);
            if(sP.exists()){
                sP.delete();
            }
            sP.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(sP));
            byte[] bytes = new byte[1024];
            int by = 0;
            while ((by = in.read(bytes)) != -1) {
                out.write(bytes, 0, by);
            }
            out.flush();
            Utils.printLogi(Utils.class.getSimpleName(), "成功复制文件");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIn(in, is);
            closeOut(out);
        }
    }


    private static void printLogi(String msg){
        Utils.printLogi(TAG, msg);
    }

    /**
     * 关闭输入流
     *
     * @param args 输入流数组
     */
    public static void closeIn(InputStream... ins){
        try {
            for (int i = 0; i < ins.length; i++) {
                if (ins[i] != null) {
                    ins[i].close();
                    ins[i] = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输出流
     *
     * @param args 输出流数组
     */
    public static void closeOut(OutputStream... outs){
        try {
            for (int i = 0; i < outs.length; i++) {
                if (outs[i] != null) {
                    outs[i].close();
                    outs[i] = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url){
        if (url == null) {
            printLogi("userIconUrl = " + url);
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(InputStream is){
        if (is == null) {
            return null;
        }
        return BitmapFactory.decodeStream(is);  // 把流转化为Bitmap图片
    }

    /**
     * 获取文件名 格式为：20140718_221839.jpg
     * @return
     */
    public static String getFileName(){
        String name = Utils.getLongDate();
        String[] names = name.replaceAll(" ", "#").split("#");
        String dateStr = names[0].replaceAll("-", "");
        String timeStr = names[1].replaceAll(":", "");
        tempPhoneFileName = new StringBuffer();
        tempPhoneFileName.append(dateStr);
        tempPhoneFileName.append("_");
        tempPhoneFileName.append(timeStr);
        tempPhoneFileName.append(".png");
        return tempPhoneFileName.toString();
    }

    /**
     * 获取Bitmap大小(在内存中占的大小是文件本身大小的4倍，所以计算实际大小时 /4)
     * @param bitmap
     * @return
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap){
        if(bitmap == null){
            return 0;
        }
        int bitmapSize = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bitmapSize = bitmap.getByteCount();
        } else {
            bitmapSize = bitmap.getRowBytes() * bitmap.getHeight(); // HC-MR1 以前
        }
        return bitmapSize;
    }

    /**
     * 获取本地图片，把url转换为Drawable
     * @param iconPath 图片地址
     * @return
     */
    public static Drawable getUserIconDrawable(String iconPath){
        if (iconPath == null) {
            return null;
        }
        File path = new File(Environment.getExternalStorageDirectory(), iconPath);
        printLogi("path = " + path.getPath());
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            if (is != null) {
                return IOFormat.getInstance().inputStream2Drawable(is);
            }
        } catch (FileNotFoundException e) {
            printLogi("用户头像地址无效: " + e.toString());
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void saveBitmap(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName){
        File file = new File(SDPATH + fileName);
        if(file.isFile()){
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

}
