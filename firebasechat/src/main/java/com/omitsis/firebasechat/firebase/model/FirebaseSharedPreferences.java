package com.omitsis.firebasechat.firebase.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * This shared preferences are used to keep session alive and user data for firebase functions
 */
public class FirebaseSharedPreferences {

    private static FirebaseSharedPreferences appSharedPreferences;

    private final static String PREFS = "FirebaseSharedPreferences";

    private final SharedPreferences mPrefs;

    private FirebaseSharedPreferences(Context context) {
        mPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized FirebaseSharedPreferences getInstance(Context context) {
        if (appSharedPreferences == null) {
            appSharedPreferences = new FirebaseSharedPreferences(context);
        }

        return appSharedPreferences;
    }

    public void setUserEmail(String email) {
        mPrefs.edit()
                .putString("userEmail", email)
                .apply();
    }

    public String getUserEmail() {
        return mPrefs.getString("userEmail", "No email");
    }

    public void setUserName(String userName) {
        mPrefs.edit()
                .putString("userName", userName)
                .apply();
    }

    public String getUserName() {
        return mPrefs.getString("userName", "No user");
    }

    public void setUserId(String userId) {
        mPrefs.edit()
                .putString("userId", userId)
                .apply();
    }

    public String getUserId() {
        return mPrefs.getString("userId", "No userId");
    }


    public void setChats(ArrayList<ChatDetails> chats) {
        Gson gson = new Gson();
        String json = gson.toJson(chats);
        mPrefs.edit()
                .putString("chats", json)
                .apply();
    }

    public ArrayList<ChatDetails> getChats() {
        Gson gson = new Gson();
        String json = mPrefs.getString("chats", "");
        return gson.fromJson(json, new TypeToken<ArrayList<ChatDetails>>() {
        }.getType());
    }

    public void deletePreferences() {
        mPrefs.edit()
                .clear()
                .apply();
    }
}

