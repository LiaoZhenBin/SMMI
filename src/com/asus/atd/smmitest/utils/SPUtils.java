package com.asus.atd.smmitest.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    public static final String TAG = SPUtils.class.getSimpleName();
    public static final String FILE_NAME = "default_smmi_results";

    public static void put(Context context, String key, Object object, String fileName) {
        //   Context context = BaseApplication.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }


    public static void put(Context context, String key, Object object) {
        put(context, key, object, FILE_NAME);

    }

    public static <T> T get(Context context, String key, T defaultObject, String fileName) {
        // Context context = BaseApplication.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Object result = null;
        if (defaultObject instanceof String) {
            result = sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            result = sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            result = sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            result = sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            result = sp.getLong(key, (Long) defaultObject);
        }

        return (T) result;
    }


    public static <T> T get(Context context, String key, T defaultObject) {
        return get(context, key, defaultObject, FILE_NAME);
    }


    /**
     * 移除某个key值已经对应的值
     * @param key
     */
 /*   public static void remove(String key, String fileName)
    {
        Context context = BaseApplication.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }
    *//**
     * 移除某个key值已经对应的值
     * @param key
     *//*
    public static void remove(String key)
    {
        remove(key, FILE_NAME);
    }


    *//**
     * 清除所有数据
     *//*
    public static void clear(String fileName)
    {
        Context context = BaseApplication.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    *//**
     * 清除所有数据
     *//*
    public static void clear()
    {
        clear(FILE_NAME);
    }

    *//**
     * 查询某个key是否已经存在
     * @param key
     * @return
     *//*
    public static boolean contains(String key, String fileName)
    {
        Context context = BaseApplication.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    *//**
     * 查询某个key是否已经存在
     * @param key
     * @return
     *//*
    public static boolean contains(String key) {
        return contains(key, FILE_NAME);
    }

    *//**
     * 返回所有的键值对
     *
     * @return
     *//*
    public static Map<String, ?> getAll(String fileName)
    {
        Context context = BaseApplication.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }


    */

    /**
     * 返回所有的键值对
     *
     * @return
     *//*
    public static Map<String, ?> getAll() {
        return getAll(FILE_NAME);
    }*/


    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }


        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}
