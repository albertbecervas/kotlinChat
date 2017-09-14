package com.omitsis.firebasechat.firebase.model;

import com.omitsis.firebasechat.firebase.interfaces.ChatDetailsInterface;

import java.util.HashMap;
import java.util.Map;

public class ChatDetails implements ChatDetailsInterface {

    private static ChatDetails chatDetails;

    private String key;
    private String chatId;
    private String otherUserId;
    private String otherUserName;

    public ChatDetails() {
        this.otherUserId = "";
        this.otherUserName = "";
        this.chatId = "";
    }

    public static synchronized ChatDetails getInstance() {
        if (chatDetails == null) {
            chatDetails = new ChatDetails();
        }

        return chatDetails;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void deleteChat() {
        chatDetails = null;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public Map<String, Object> setChatHashMapForDB() {

        Map<String, Object> map = new HashMap<>();
        map.put("key", getKey());
        map.put("chatId", getChatId());
        map.put("otherUserId", getOtherUserId());
        map.put("otherUserName", getOtherUserName());

        return map;
    }
}

