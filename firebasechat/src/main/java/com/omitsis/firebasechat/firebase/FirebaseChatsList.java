package com.omitsis.firebasechat.firebase;

import android.content.Context;
import android.content.Intent;

import com.omitsis.firebasechat.firebase.model.ChatDetails;
import com.omitsis.firebasechat.firebase.service.ChatsService;

import java.util.ArrayList;

/**
 * Base class to get the list of active chats of our users
 */
public class FirebaseChatsList {

    private ChatsService mService;

    public FirebaseChatsList() {
    }

    /**
     * Starts a service to listen for new chats opened
     *
     * @param context to get Shared Preferences
     */
    public void startChatsService(Context context) {
        mService = new ChatsService();

        Intent intent = new Intent(context, ChatsService.class);
        context.startService(intent);

        mService.startListeners(context);
    }

    /**
     * Sets and object with the information of the user clicked in order to use it on chat screen
     *
     * @param adapterPosition to get the current chat that user has entered
     * @param chats           arraylist of user active chats
     */
    public void setChat(int adapterPosition, ArrayList<ChatDetails> chats) {
        ChatDetails mChat = ChatDetails.getInstance();
        mChat.setOtherUserName(chats.get(adapterPosition).getOtherUserName());
        mChat.setChatId(chats.get(adapterPosition).getChatId());
        mChat.setOtherUserId(chats.get(adapterPosition).getOtherUserId());
        mChat.setKey(chats.get(adapterPosition).getKey());
    }

    /**
     * removes all listeners to avoid conflicts when screen is left
     */
    public void removeListeners() {
        mService.removeListeners();
    }

}
