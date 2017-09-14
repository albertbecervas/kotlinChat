package com.omitsis.firebasechat.firebase;

import com.omitsis.firebasechat.firebase.model.ChatDetails;

import java.util.ArrayList;

public interface ChatCallback {

    void setStatus(String status);

    void onNewMessageAdded(String message, String date, int type);

    void updateUsername(String username);

    void updateUserDataExtension(ArrayList<ChatDetails> chats);
}
