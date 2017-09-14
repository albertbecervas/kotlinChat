package com.omitsis.firebasechat.firebase;

import com.google.firebase.database.DataSnapshot;
import com.omitsis.firebasechat.firebase.model.ChatDetails;

import java.util.ArrayList;

public interface LoginCallback {

    void onLoginSucced(ArrayList<ChatDetails> chats, String username,String userId, boolean isAdmin, DataSnapshot dataSnapshot);
    void onLoginFailed(String error);

}
