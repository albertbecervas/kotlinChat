package com.omitsis.firebasechat.firebase;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.omitsis.firebasechat.firebase.model.ChatDetails;
import com.omitsis.firebasechat.firebase.model.FirebaseSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class which controls all chat actions like sending a message, updating profile information,
 * setting online/offline listener and finding if chat already exists
 * <p>
 * In case you have different screens for chats and users, you may provide the intent in order to
 * check or not if the chat has been started because if you are coming from chats screen, you won't
 * have to check for it
 * <p>
 * You may provide the context in order to set the Shared Preferences that this library implements
 */
public class FirebaseChat {

    private ChatDetails mChat;
    private Map<String, Object> mChatMap;
    private HashMap<String, Object> mUserExpansion;

    private ChatCallback mCallback;

    private FirebaseSharedPreferences mPrefs;

    private ChildEventListener mListener;
    private DatabaseReference mDatabase;
    private DataSnapshot mDatasnapshot;

    private boolean exists;

    /**
     * Public constructor
     * Instantiates users model
     * Checks if you are coming from chats screen, otherwise checks if you have started a chat with
     * that user in order to create or not a new entrance in database when writing the first message.
     * Checks if the user name or image has been updated.
     *
     * @param context to set SharedPreferences user model.
     * @param intent  to know whether you are coming from chats or not
     * @param map     Map to expand the basic chat information in database
     */
    public FirebaseChat(Context context, Intent intent, Map<String, Object> map, HashMap<String, Object> userExpansion) {
        mChat = ChatDetails.getInstance();
        mPrefs = FirebaseSharedPreferences.getInstance(context);
        mCallback = (ChatCallback) context;

        if (!intent.hasExtra("from_chats")) {
            this.mChatMap = map;
            this.mUserExpansion = userExpansion;
            checkChatId();
        } else {
            exists = true;
            setMessagesListener();
        }

        checkForUpdates();

    }

    /**
     * Listener to know if user is online or offline checking the online boolean on database
     */
    public void setOnlineListener() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mChat.getOtherUserId()).child("online").getRef();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().equals("true"))
                        mCallback.setStatus("online");
                    else
                        mCallback.setStatus("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Gets the current date
     * In case chat already exists sets the new message on database
     * In case chat doesn't exists:
     * creates the message structure
     * creates a new chat on our user and creates the same chat in the other user on dabatabase
     * then sets the messages listener
     *
     * @param message message text that user wants to send
     */
    public void setMessage(String message) {
        Date currentDate = new Date();

        SimpleDateFormat date = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        String stringDate = date.format(currentDate);

        if (exists) {
            //do nothing because chat already exists
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chats");

            final Map<String, String> map = new HashMap<>();
            map.put("senderName", mPrefs.getUserName());
            map.put("senderId", mPrefs.getUserId());
            map.put("text", message);
            map.put("date", stringDate);

            mDatabase.child(mChat.getChatId()).child("messages").push().setValue(map);

        } else {
            //we create a new chat on database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chats");

            final Map<String, String> map = new HashMap<>();
            map.put("senderName", mPrefs.getUserName());
            map.put("senderId", mPrefs.getUserId());
            map.put("text", message);
            map.put("date", stringDate);

            String key = mDatabase.push().child("messages").push().getKey();
            mChat.setChatId(key);
            mDatabase.child(key).child("messages").push().setValue(map);

            mDatabase = FirebaseDatabase.getInstance().getReference("users");

            setOwnChatArray(mDatabase, key);
            setOtherChatArray(mDatabase, key);

            exists = true;

            setMessagesListener();

        }
    }

    /**
     * Erases all listeners and chat details
     */
    public void onDestroy() {
        mChat.deleteChat();
        if (mDatabase != null) mDatabase.removeEventListener(mListener);
    }

    /**
     * Looks into the other user object to know whether the user image or user name has been updated
     * Then updates all information we have saved on sharedPreferences and database ( like the user name
     * in our own chat array )
     * Finally sets the name and user image
     */
    private void checkForUpdates() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(mChat.getOtherUserId()).getRef();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (!dataSnapshot.child("username").getValue().equals(mChat.getOtherUserName())) {

                        ArrayList<ChatDetails> chats = mPrefs.getChats();

                        int i = 0;
                        for (ChatDetails chatDetails : mPrefs.getChats()) {
                            if (chatDetails.getOtherUserName().equals(mChat.getOtherUserName())) {
                                chats.get(i).setOtherUserName(dataSnapshot.child("username").getValue(String.class));
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mPrefs.getUserId()).child("activeChats").child(mChat.getKey()).getRef();
                                mDatabase.setValue(chats.get(i));
                                mPrefs.setChats(chats);
                                mCallback.updateUserDataExtension(chats);
                            }
                            i++;
                        }
                    }

                    mDatasnapshot = dataSnapshot;
                    mCallback.updateUsername(dataSnapshot.child("username").getValue(String.class));

                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public Object getChatDetailsExtension(String childName) {
        return mDatasnapshot.child(childName).getValue();
    }

    /**
     * Message listener to set every new message is sent on current chat
     */
    private void setMessagesListener() {
        mDatabase = FirebaseDatabase.getInstance().getReference("chats").child(mChat.getChatId()).child("messages").getRef();

        mListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot);
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

        mDatabase.addChildEventListener(mListener);
    }

    private void setOwnChatArray(DatabaseReference mDatabase, String newChatId) {
        //si el array de chats del usuario está vacio creamos uno nuevo, sino, cogemos el anterior y lo actualizamos
        ArrayList<ChatDetails> chats;
        if (mPrefs.getChats() != null) {
            chats = mPrefs.getChats();
        } else {
            chats = new ArrayList<>();
        }

        String key = mDatabase.child(String.valueOf(mPrefs.getUserId())).child("activeChats").push().getKey();
        chats.add(mChat);

        mUserExpansion.put("otherUserId", mChat.getOtherUserId());
        mUserExpansion.put("otherUserName", mChat.getOtherUserName());
        mUserExpansion.put("chatId", newChatId);

        mDatabase.child(String.valueOf(mPrefs.getUserId())).child("activeChats").child(key).setValue(mUserExpansion);
        mChat.setKey(key);
    }

    private void setOtherChatArray(DatabaseReference mDatabase, String newChatID) {
        //creamos otro Chat ahora con nuestros datos y la misma id para el otro usuario
        mChatMap.put("otherUserId", mPrefs.getUserId());
        mChatMap.put("otherUserName", mPrefs.getUserName());
        mChatMap.put("chatId", newChatID);

        mDatabase.child(mChat.getOtherUserId()).child("activeChats").push().setValue(mChatMap);
    }

    /**
     * Check if we have started a chat with that user
     */
    private void checkChatId() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.orderByChild("email").equalTo(String.valueOf(mChat.getOtherUserName())).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //miramos si tenemos un chat con ese username
                lookForChatStarted();
                mDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void lookForChatStarted() {
        exists = false;
        if (mPrefs.getChats() != null) {
            for (ChatDetails chat : mPrefs.getChats()) {
                if (chat.getOtherUserName().equals(mChat.getOtherUserName())) {
                    exists = true;
                    mChat.setChatId(chat.getChatId());
                    mChatMap.put("chatId", chat.getChatId());
                    break;
                }
            }
        }
        //set del listener en caso de que exista el chat y vengamos de users
        if (exists) setMessagesListener();
    }

    /**
     * Adds the new message box
     * Depending on the message sender we set the box in the right side ( type 1 ) or the left one
     * ( type 2 )
     *
     * @param dataSnapshot to get the message and user information
     */
    private void addMessage(com.google.firebase.database.DataSnapshot dataSnapshot) {
        GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {
        };
        Map<String, String> map = dataSnapshot.getValue(t);

        if (map != null) {
            String message = map.get("text");
            String userId = map.get("senderId");
            String date = map.get("date");

            if (userId.equals(mPrefs.getUserId())) {
                mCallback.onNewMessageAdded(message, date, 1);
            } else {
                mCallback.onNewMessageAdded(message, date, 2);
            }
        }
    }
}
