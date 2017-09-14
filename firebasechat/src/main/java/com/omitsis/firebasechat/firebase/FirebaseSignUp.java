package com.omitsis.firebasechat.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.omitsis.firebasechat.firebase.model.FirebaseSharedPreferences;

import java.util.Map;

/**
 * Base class for sign up a user with firebase authentication
 * You can sign up via Facebook, Twitter, Email and password and Google
 * <p>
 * You may provide the Context in order to save the user in SharedPreferences
 */
public class FirebaseSignUp {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Context mContext;

    private FirebaseSharedPreferences mUser;

    private SignupCallback mCallback;

    /**
     * Default constructor
     * Set instance of FirebaseSharedPreferences object.
     * Set authentication listener
     * Set Database reference
     *
     * @param context needed to instantiate Shared Preferences
     */
    public FirebaseSignUp(Context context, DatabaseReference databaseReference) {
        this.mContext = context;

        mUser = FirebaseSharedPreferences.getInstance(context);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(setAuthListener());

        mDatabase = databaseReference.child("users");

        mCallback = (SignupCallback) mContext;
    }


    /**
     * Sets the authentication listener.
     * When user is signed in it saves in Shared Preferences the user E-mail and Status.
     *
     * @return authentication listener
     */
    private FirebaseAuth.AuthStateListener setAuthListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.getEmail() != null) {
                        // User is signed in
//                        mUser.setUserId(user.getUid());

                        mUser.setUserEmail(user.getEmail());

                        mAuth.removeAuthStateListener(this);
                    }
                }
            }
        };
    }

    /**
     * Sign up with E-mail and password authentication
     * <p>
     * Saves in Shared Preferences the user chats, id, username, and image url if the user
     * is correctly signed in.
     *
     * @param map      Map to set the user entered e-mail.
     * @param password String to set the user entered password
     */
    public void CreateAuthUserWithEmailAndPassword(final Map<String, Object> map,final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser.setUserId(task.getResult().getUser().getUid());
                            mUser.setUserEmail(email);
                            signUp(map);
                            mCallback.onSignUpSucceed();
                        } else {
                            mCallback.onSignUpFailed();
                        }
                    }
                });
    }

    private void signUp(Map<String, Object> map) {

//        String key = mDatabase.child("users").push().getKey();
        mDatabase.child(mUser.getUserId()).setValue(map);

//        mUser.setUserId(String.valueOf(key));
        mUser.setUserName(map.get("username").toString());
    }

}
