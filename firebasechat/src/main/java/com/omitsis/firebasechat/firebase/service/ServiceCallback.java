package com.omitsis.firebasechat.firebase.service;

import com.google.firebase.database.DataSnapshot;
import com.omitsis.firebasechat.firebase.model.ChatDetails;

import java.util.ArrayList;

public interface ServiceCallback {

    void onChatAdded(ArrayList<ChatDetails> chats, DataSnapshot dataSnapshot);

}
