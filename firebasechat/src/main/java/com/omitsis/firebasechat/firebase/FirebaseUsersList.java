package com.omitsis.firebasechat.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omitsis.firebasechat.firebase.model.ChatDetails;
import com.omitsis.firebasechat.firebase.model.UsersDetails;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class to get all users
 */
public class FirebaseUsersList {

    private DatabaseReference mDatabase;

    private ValueEventListener postListener;

    private UsersCallback mCallback;

    private String ownUsername;

    private DataSnapshot mChild;

    /**
     * Empty constructor to be used from adapter
     */
    public FirebaseUsersList() {
    }

    /**
     * Base constructor that provides a callback
     *
     * @param context     context to callback
     * @param ownUsername String to get username
     */
    public FirebaseUsersList(Context context, String ownUsername) {
        mCallback = (UsersCallback) context;
        this.ownUsername = ownUsername;
    }

    /**
     * Firebase function to get all users from database
     * When we get all the users data we callback the activity giving the arraylist of users
     */
    public void getUsers() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        postListener = setValueEventListener();
        mDatabase.addValueEventListener(postListener);
    }

    @NonNull
    private ValueEventListener setValueEventListener() {
        return new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<UsersDetails> users = new ArrayList<>();
                int totalUsers = 0;

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String userEmail = String.valueOf(child.child("email").getValue());
                    if (!userEmail.equals(ownUsername)) {
                        UsersDetails usersDetails = new UsersDetails();
                        usersDetails.setUsername(String.valueOf(child.child("username").getValue()));
                        usersDetails.setKey(child.getKey());
                        usersDetails.setEmail(userEmail);
                        users.add(usersDetails);
                        mChild = child;
                        mCallback.onLoadUserDetailsExpansion(child);
                    }
                    totalUsers++;
                }

                mCallback.onUsersLoaded(users, totalUsers, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public Object getChild(String childName) {
        return mChild.child(childName).getValue();
    }


    /**
     * Sets the other chat user when you select a new one from the adapter
     * This function may be used on adapter to know which position the user has clicked and load the
     * right data
     *
     * @param users           ArrayList of users
     * @param adapterPosition Int position to get the current user position on the users array
     */
    public HashMap<String, Object> setChat(int adapterPosition, ArrayList<UsersDetails> users, HashMap<String, Object> map) {

        ChatDetails mChat = ChatDetails.getInstance();
        mChat.setOtherUserName(users.get(adapterPosition).getUsername());
        mChat.setOtherUserId(users.get(adapterPosition).getKey());

        map.put("otherUserName", users.get(adapterPosition).getUsername());
        map.put("otherUserId", users.get(adapterPosition).getKey());

        return map;
    }

    public void removeUser(String uid){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).removeValue();
    }

    /**
     * Removes all listeners in order to avoid conflicts between screens
     */
    public void removeListeners() {
        if (mDatabase != null)
            mDatabase.removeEventListener(postListener);
    }
}
