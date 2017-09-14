package com.omitsis.firebasechat.firebase.helper;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.omitsis.firebasechat.firebase.model.FirebaseSharedPreferences;

public class FirebaseChatHelper {

    private FirebaseSharedPreferences mPrefs;
    private DatabaseReference mDatabase;

    public FirebaseChatHelper() {
    }

    public FirebaseChatHelper(Context context) {
        mPrefs = FirebaseSharedPreferences.getInstance(context);
    }

    public FirebaseChatHelper(Context context, DatabaseReference mDatabase) {
        mPrefs = FirebaseSharedPreferences.getInstance(context);
        this.mDatabase = mDatabase;
    }

    public boolean isUserLogedIn() {
        return !mPrefs.getUserEmail().equals("No user");
    }

    public void setUserStatusOnline(String online) {
        mDatabase.child("users").child(mPrefs.getUserId()).child("online").setValue(online);
    }

    public String getUserKey(String username){
        return mDatabase.child("users").orderByChild(username).equalTo(username).getRef().getKey();
    }
}
