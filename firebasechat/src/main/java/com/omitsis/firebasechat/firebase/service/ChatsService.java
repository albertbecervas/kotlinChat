package com.omitsis.firebasechat.firebase.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omitsis.firebasechat.firebase.model.ChatDetails;
import com.omitsis.firebasechat.firebase.model.FirebaseSharedPreferences;

import java.util.ArrayList;

/**
 * Main service listening for new chats and messages
 * Sets two listeners. The ValueEventListener is used to know if we have finished adding all the chats
 * on ChildEventListener
 */
public class ChatsService extends IntentService {

    private ServiceCallback mCallback;

    private FirebaseSharedPreferences mUser;

    private ArrayList<ChatDetails> userChats;

    private DatabaseReference mDatabase;

    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    /**
     * Base constructor
     */
    public ChatsService() {
        super("chats");
        userChats = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    /**
     * Starts all listeners to update all chats in case of a new chat created on your chats array on
     * database
     *
     * @param context context for activity callback and user instance
     */
    public void startListeners(Context context) {
        mUser = FirebaseSharedPreferences.getInstance(context);

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUserId()).child("activeChats").getRef();

        mCallback = (ServiceCallback) context;

        childEventListener = setChildEventListener();

        valueEventListener = setValueEventListener();
        mDatabase.addChildEventListener(childEventListener);
        mDatabase.addValueEventListener(valueEventListener);
    }

    @NonNull
    private ValueEventListener setValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    setUserChats(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * If we have no chats and datasnapshot contains a chat we set directly the chat array
     * In case we have some chats we compare datasnapshot size with our own chat array in order to
     * know if a new chat has been created
     *
     * @param dataSnapshot contains the chat array from database
     */
    private void setUserChats(DataSnapshot dataSnapshot) {
        if (!mUser.getChats().isEmpty()) {
            if (dataSnapshot.getChildrenCount() != mUser.getChats().size()) {
                ArrayList<ChatDetails> aux = new ArrayList<>();
                for (ChatDetails chatDetails : userChats) {
                    aux.add(chatDetails);
                }
                mUser.setChats(aux);
                mCallback.onChatAdded(aux, dataSnapshot);
            }
        } else {
            mUser.setChats(userChats);
            mCallback.onChatAdded(userChats, dataSnapshot);
        }
    }

    @NonNull
    private ChildEventListener setChildEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    setChatsArray(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Sets all chats information
     *
     * @param dataSnapshot to get the arraylist of chat details from users chat arraylist on database
     */
    private void setChatsArray(DataSnapshot dataSnapshot) {

        ChatDetails userChat = new ChatDetails();
        userChat.setKey(dataSnapshot.getKey());
        userChat.setChatId(dataSnapshot.child("chatId").getValue(String.class));
        userChat.setOtherUserId(dataSnapshot.child("otherUserId").getValue(String.class));
        userChat.setOtherUserName(dataSnapshot.child("otherUserName").getValue(String.class));

        userChats.add(userChat);
    }

    /**
     * Removes all listeners to avoid conflicts between activities
     */
    public void removeListeners() {
        if (mDatabase != null) {
            mDatabase.removeEventListener(childEventListener);
            mDatabase.removeEventListener(valueEventListener);
        }
    }
}
