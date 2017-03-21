package com.asus.atd.smmitest.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FileLog {
    public static final String folder = "/SMMI_TestResult/";

    private static String getEasyTime() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getEasyTime(Date date) {
        return new SimpleDateFormat("MM-dd HH:mm:ss ").format(date);
    }

    private static String getSecondFolder() {
        return getEasyTime() + "/";
    }

    private static String getLogTimeName() {
        return getEasyTime() + ".txt";
    }

    private static String getEzLogFilePath() {
        String fileName = "Log-" + getLogTimeName();
        return getEzLogPath(fileName);
    }

    private static String getExLogFilePath(String level) {
        return getExLogPath("Exlog-" + level + "-" + getLogTimeName());
    }

    private static String getExLogPath(String fileName) {
        return getExLogPath() + fileName;
    }
    public static String getExLogPath() {
        return Environment.getExternalStorageDirectory() + folder + getSecondFolder();
    }

    public static String getEzLogPath(String fileName) {
        return Environment.getExternalStorageDirectory() + folder + fileName;
    }


    public static List<String> getAllLogsName() {
        String logFolder = Environment.getExternalStorageDirectory() + folder;
        File file = new File(logFolder);
        String[] strings = file.list();
        List<String> logs = null;
        if (strings != null && strings.length > 0) {
            logs = new ArrayList<String>(Arrays.asList(strings));
            List<String> deleteList = new ArrayList<String>();
            for (String log : logs) {
                if (!log.contains(".")) {
                    deleteList.add(log);
                }
            }
            logs.removeAll(deleteList);
            Collections.sort(logs, Collections.reverseOrder());
        }
        return logs;
    }

    public static String getNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static void logToExternalFile(String msg, String path) {
        StringBuffer buffer = new StringBuffer();
        String msgToWrite = buffer.append("\r\n").append(getNow()).append(" ").append(": ").append(msg).toString();
        synchronized (FileLog.class) {
            if (FileUtils.prepareFile(path)) {
                try {
                    FileOutputStream stream = new FileOutputStream(path, true);
                    stream.write(msgToWrite.getBytes("utf-8"));
                    stream.close();
                    // 此处不可写入本地
                    // Log.v(LogUtils.class.getSimpleName(), msgToWrite);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void easyLogToExternalFile(String msg) {
        logToExternalFile(msg, getEzLogFilePath());
    }

    public static void exLogToExternalFile(String level, String tag, String msg) {
        logToExternalFile("@" + tag + "@" + msg, getExLogFilePath(level));
    }

    @Deprecated
    public static void logI(String tag, String msg) {
        exLogToExternalFile("i", tag, msg);
        Log.i(tag, msg);

    }

    @Deprecated
    public static void logE(String tag, String msg) {
        exLogToExternalFile("e", tag, msg);
        Log.e(tag, msg);

    }
}
