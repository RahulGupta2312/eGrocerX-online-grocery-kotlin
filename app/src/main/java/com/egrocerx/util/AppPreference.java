package com.egrocerx.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.egrocerx.R;
import com.egrocerx.core.MyApplication;
import com.egrocerx.data.UserModel;

import org.jetbrains.annotations.NotNull;


public class AppPreference {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static AppPreference instancePreferences;

    class PREF_KEYS {

        static final String USER_DATA = "user_data";
        static final String USER_ID = "user_id";
        static final String ISLOGGED_IN = "logged_in";
        static final String FCM_TOKEN = "fcm_token";
    }

    private static void init(Context context) {
        instancePreferences = new AppPreference();
        preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

        editor = preferences.edit();
    }

    public static AppPreference getInstance() {
        if (instancePreferences == null) {
            init(MyApplication.instance.getContext());
        }
        return instancePreferences;
    }

    private void storeString(@NonNull String key, String value) {

        editor.putString(key, value);

        editor.apply();
    }

    private String getString(@NotNull String key) {

        return preferences.getString(key, null);
    }

    private void storeBoolean(@NotNull String key, boolean value) {

        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean getBoolean(@NotNull String key) {
        return preferences.getBoolean(key, false);
    }

    private void storeInt(String key, int value) {

        editor.putInt(key, value);
        editor.apply();
    }

    private int getInt(@NotNull String key) {
        return preferences.getInt(key, 0);
    }

    private void storeFloat(@NotNull String key, float value) {

        editor.putFloat(key, value);
        editor.apply();
    }

    private float getFloat(@NotNull String key) {
        return preferences.getFloat(key, 0.0f);
    }

    private boolean keyExists(@NotNull String key) {
        return preferences.contains(key);
    }

    private void deleteKey(@NotNull String key) {
        editor.remove(key);
        editor.apply();
    }

    public String getUserId() {
        return getString(PREF_KEYS.USER_ID);
    }

    public void saveUserId(String citySlug) {
        storeString(PREF_KEYS.USER_ID, citySlug);
    }

    public boolean getLoggedIn() {
        return getBoolean(PREF_KEYS.ISLOGGED_IN);
    }

    public void saveLoggedIn(boolean value) {
        storeBoolean(PREF_KEYS.ISLOGGED_IN, value);
    }
    public String getFcmToken() {
//        if(TextUtils.isEmpty(getString(PREF_KEYS.FCM_TOKEN)))
//            return "" ;
//        else
            return getString(PREF_KEYS.FCM_TOKEN);
    }

    public void saveFcmToken(String value) {
        storeString(PREF_KEYS.FCM_TOKEN, value);
    }

    public void saveUserData(String data) {
        storeString(PREF_KEYS.USER_DATA, data);
    }

    public UserModel getUserData() {
        return new Gson().fromJson(getString(PREF_KEYS.USER_DATA), UserModel.class);
    }

    public void clearAllPreferences() {
        deleteKey(PREF_KEYS.USER_DATA);
        deleteKey(PREF_KEYS.USER_ID);
        deleteKey(PREF_KEYS.ISLOGGED_IN);
    }
}
