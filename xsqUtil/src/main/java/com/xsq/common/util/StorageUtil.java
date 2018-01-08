package com.xsq.common.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/6 0006.
 */
public class StorageUtil {

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获得下载目录路径
     *
     * @return 外置内存卡下载路径
     */
    public static String getExternalDownloadPath() {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取SDCard下关联应用包路径
     * @return
     */
    public static String getSDCardContextPackagePath(){
        return getSDCardPath() + AppUtil.getAppPackageName();
    }

//    /**
//     * 多个SD卡时 取外置SD卡
//     * @return
//     */
//    public static String getExternalStorageDirectory() {
//        // 参考文章
//        // http://blog.csdn.net/bbmiku/article/details/7937745
//        Map<String, String> map = System.getenv();
//        String[] values = new String[map.values().size()];
//        map.values().toArray(values);
//        String path = values[values.length - 1];
//        if (path.startsWith("/mnt/") && !Environment.getExternalStorageDirectory()
//                .getAbsolutePath()
//                .equals(path)) {
//            return path;
//        } else {
//            return null;
//        }
//    }


}
