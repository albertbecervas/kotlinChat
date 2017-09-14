package com.omitsis.firebasechat.firebase;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.omitsis.firebasechat.firebase.model.FirebaseSharedPreferences;

/**
 * Base class to update firebase user data
 */
public class FirebaseProfile {

    private DatabaseReference mDatabase;

    private FirebaseSharedPreferences mUser;

    private String userId;

    public FirebaseProfile(DatabaseReference mDatabase, Context context) {
        this.mDatabase = mDatabase;
        mUser = FirebaseSharedPreferences.getInstance(context);
        this.userId = mUser.getUserId();
    }

    public void setImage(String url) {
        mDatabase.child("users").child(userId).child("imageUrl").setValue(url);
    }

    public void setUserName(String userName) {
        mDatabase.child("users").child(userId).child("username").setValue(userName);
        mUser.setUserName(userName);
    }

    /**
     * removes all user data stored
     */
    public void onLogOut() {
        mUser.deletePreferences();
    }
}
