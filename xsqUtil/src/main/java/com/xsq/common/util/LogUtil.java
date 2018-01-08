package com.xsq.common.util;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2015/12/31 0031.
 */
public class LogUtil {

    public static enum LogLevel {
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3),
        OFF(99);

        private final int level;

        private LogLevel(int val) {
            this.level = val;
        }

        public int getLevel() {
            return level;
        }

        public boolean isLe(LogLevel level) {
            if (getLevel() <= level.getLevel()) {
                return true;
            }
            return false;
        }

    }

    private static String tagName = "LogUtil";
    //0=debug,1=info,2=warn,3=error
    private static LogLevel currentLevel = LogLevel.INFO;
    private static LogLevel fileLogLevel = LogLevel.WARN;
    private static String fileLogName = "appInfo";
    private static String fatalLogName = "fatal";
    private static String fileLogExtension = ".log";
    private static String finalLogFullName = fileLogName + fileLogExtension;
    private static String finalFatalLogFullName = fatalLogName + fileLogExtension;
    private static long fileLogMaxSize = 10485760;//10M大小超出大小将新建文件块
    private static int fileSaveBlock = 1;//文件块保留大小超出文件块将被删除
    private static String fileSavePath = StorageUtil.getSDCardContextPackagePath();

    private static Lock fileWriteLock = new ReentrantLock();

    public static LogLevel getCurrentLogLevel() {
        return currentLevel;
    }

    public static void updateCurrentLogLevel(LogLevel newLevel) {
        if (newLevel != null) {
            currentLevel = newLevel;
        }
    }

    public static void updateFileLogLevel(LogLevel newLevel){
        if(newLevel != null){
            fileLogLevel = newLevel;
        }
    }

    public static void debug(String content) {
        debug(content, null);
    }

    public static void debug(String content, Throwable tr) {
        debug(tagName, content, tr);
    }

    public static void debug(String tag, String content, Throwable tr) {
        if (currentLevel.isLe(LogLevel.DEBUG)) {
            if (tr == null) {
                Log.d(tag, content);
            } else {
                Log.d(tag, content, tr);
            }
        }

        if (fileLogLevel.isLe(LogLevel.DEBUG)) {
            handFileLog("DEBUG", tag, content, tr, false);
        }
    }

    public static void info(String content) {
        info(content, null);
    }

    public static void info(String content, Throwable tr) {
        info(tagName, content, tr);
    }

    public static void info(String tag, String content, Throwable tr) {
        if (currentLevel.isLe(LogLevel.INFO)) {
            if (tr == null) {
                Log.i(tag, content);
            } else {
                Log.i(tag, content, tr);
            }
        }

        if (fileLogLevel.isLe(LogLevel.INFO)) {
            handFileLog("INFO", tag, content, tr, false);
        }
    }

    public static void warn(String content) {
        warn(content, null);
    }

    public static void warn(String content, Throwable tr) {
        warn(tagName, content, tr);
    }

    public static void warn(String tag, String content, Throwable tr) {
        if (currentLevel.isLe(LogLevel.WARN)) {
            if (tr == null) {
                Log.w(tag, content);
            } else {
                Log.w(tag, content, tr);
            }
        }

        if (fileLogLevel.isLe(LogLevel.WARN)) {
            handFileLog("WARN", tag, content, tr, false);
        }

    }

    public static void error(String content) {
        error(content, null);
    }

    public static void error(String content, Throwable tr) {
        error(tagName, content, tr);
    }

    public static void error(String tag, String content, Throwable tr) {
        if (currentLevel.isLe(LogLevel.ERROR)) {
            if (tr == null) {
                Log.e(tag, content);
            } else {
                Log.e(tag, content, tr);
            }
        }

        if (fileLogLevel.isLe(LogLevel.ERROR)) {
            handFileLog("ERROR", tag, content, tr, false);
        }

    }

    public static void logFatalToFile(String tag, Throwable tr){
        handFileLog("FATAL", tag, "", tr, true);
    }

    private static void handFileLog(String level, String tag, String content, Throwable tr, Boolean isFatal) {
        if (StorageUtil.isSDCardEnable() == false)
            return;

        StringBuilder logOutText = new StringBuilder();
        Date logTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(
                " [yyyy年MM月dd日 HH:mm:ss] ");

        if (tag == null) {
            tag = tagName;
        }

        if (content == null) {
            content = "";
        }

        logOutText.append(level);
        logOutText.append(formatter.format(logTime));
        logOutText.append(tag).append(":").append(content);

        if (tr != null) {
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            tr.printStackTrace(pw);

            String errorOut = writer.toString();
            logOutText.append("\n").append(errorOut);
        }

        logOutText.append("\n");

        writeLogToFile(tag, logOutText, isFatal);
    }

    private static void writeLogToFile(String tag, StringBuilder content, Boolean isFatal) {
        fileWriteLock.lock();
        try {
            File fileDir = new File(fileSavePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File logFile = new File(fileDir, (isFatal == false)?finalLogFullName:finalFatalLogFullName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e(tag, "handFileLog error.", e);
                }
            }

            long fileSize = logFile.length();
            if (fileSize >= fileLogMaxSize) {
                renameLogFile(0, fileDir, logFile, isFatal);
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e(tag, "handFileLog error.", e);
                }
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(logFile,
                        true);

                fileOutputStream.write(content.toString().getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.e(tag, "handFileLog error.", e);
            } catch (IOException e) {
                Log.e(tag, "handFileLog error.", e);
            }
        }finally {
            fileWriteLock.unlock();
        }

    }

    private static void renameLogFile(int blockIndex, File fileDir, File srcFile, Boolean isFatal) {
        if (blockIndex >= fileSaveBlock) {
            srcFile.delete();
            return;
        }

        File existLogFile = new File(fileDir, (isFatal == false)?fileLogName:fatalLogName + "." + blockIndex + fileLogExtension);
        if (existLogFile.exists()) {
            renameLogFile(blockIndex + 1, fileDir, existLogFile, isFatal);

            if (existLogFile.exists() == false && srcFile.exists()) {
                srcFile.renameTo(existLogFile);
            }
        } else {
            srcFile.renameTo(existLogFile);
        }

    }

}
