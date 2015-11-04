package com.brik.chat.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class SystemSettings {

    public static final String SERVICE_HOST = "http://mp.ichezhen.com";
    private static SystemSettings instance;

    public static final String SDCARD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();

    public static final String CHAT_ROOT_DIR = SDCARD_PATH + "/chat";

    public static final String TEMP_ROOT_DIR = CHAT_ROOT_DIR + "/temp";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private SystemSettings(Context context, String file) {
        Log.d("ss", "new");
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SystemSettings getInstance(Context context) {
        if(instance==null) {
            instance = new SystemSettings(context, "settings");
        }
        return instance;
    }

    public void setUsername(String username) {
        editor.putString("username", username);
        editor.commit();
    }

    public String getUsername() {
        return sp.getString("username", null);
    }

    public void setPassword(String password) {
        editor.putString("password", password);
        editor.commit();
    }

    public String getPassword() {
        return sp.getString("password", null);
    }

    public void setUid(int uid) {
        editor.putInt("uid", uid);
        editor.commit();
    }

    public int getUid() {
        return sp.getInt("uid", 0);
    }

    public void setDeviceToken(String token) {
        editor.putString("deviceToken", token);
        editor.commit();
    }

    public String getDeviceToken() {
        return sp.getString("deviceToken", null);
    }

    public void setAccountId(String accountId) {
        editor.putString("accountId", accountId);
        editor.commit();
    }

    public String getAccountId() {
        return sp.getString("accountId", null);
    }

    public static String getProp(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Object result = appInfo.metaData.get(key);
            if(result!=null)
                return result.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    
}