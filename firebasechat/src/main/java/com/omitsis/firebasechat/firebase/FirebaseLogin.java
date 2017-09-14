package com.omitsis.firebasechat.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.omitsis.firebasechat.firebase.model.ChatDetails;
import com.omitsis.firebasechat.firebase.model.FirebaseSharedPreferences;

import java.util.ArrayList;

/**
 * Base class for sign in a user with firebase authentication
 * You can sign in via Facebook, Twitter, Email and password and Google
 * <p>
 * You may provide the Context in order to save the user in SharedPreferences
 */
public class FirebaseLogin {

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseSharedPreferences mUser;

    private LoginCallback mCallback;

    /**
     * Default constructor
     * Set instance of FirebaseSharedPreferences object.
     * Set authentication listener
     *
     * @param context needed to instantiate Shared Preferences
     */
    public FirebaseLogin(Context context) {
        mContext = context;

        mCallback = (LoginCallback) context;

        mUser = FirebaseSharedPreferences.getInstance(context);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(setAuthListener());
    }

    /**
     * Sign in with E-mail and password authentication
     * <p>
     * Saves in Shared Preferences the user chats, id, username, and image url if the user
     * is correctly signed in.
     *
     * @param email    String to set the user entered e-mail.
     * @param password String to set the user entered password
     */
    public void signInAuthUserWithEmailAndPassword(final String email, String password, final DatabaseReference databaseReference) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ArrayList<ChatDetails> chats = new ArrayList<>();
//                                    for (DataSnapshot ds : dataSnapshot.getChildren().iterator().next().child("activeChats").getChildren()) {
//                                        ChatDetails chatDetails = new ChatDetails();
//                                        chatDetails.setKey(ds.getKey());
////                                        chatDetails.setChatId(ds.child("chatId").getValue(String.class));
////                                        chatDetails.setOtherUserId(ds.child("otherUserId").getValue(String.class));
////                                        chatDetails.setOtherUserName(ds.child("otherUserName").getValue(String.class));
//                                        chats.add(chatDetails);
//                                    }
//                                    mUser.setChats(chats);
//                                    mUser.setUserId(dataSnapshot.getChildren().iterator().next().getKey());
//                                    mUser.setUserName(dataSnapshot.getChildren().iterator().next().child("username").getValue(String.class));

                                    mUser.setUserId(task.getResult().getUser().getUid());

                                    Boolean isAdmin = dataSnapshot.child(mUser.getUserId()).hasChild("isAdmin");


                                    mCallback.onLoginSucced(chats, mUser.getUserName(),mUser.getUserId(), isAdmin ,dataSnapshot);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mCallback.onLoginFailed(databaseError.getMessage());
                                }
                            });
                        }
                        else {
                            mCallback.onLoginFailed("");
                        }
                    }
                });
    }

    /**
     * Sets the authentication listener.
     * When user is signed in it saves in Shared Preferences the user E-mail and Status.
     *
     * @return authentication listener
     */
    private FirebaseAuth.AuthStateListener setAuthListener() {
        return mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mUser.setUserEmail(user.getEmail());
//                    mUser.setUserId(user.getUid());
                }
            }
        };
    }
}
