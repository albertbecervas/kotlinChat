package com.omitsis.firebasechat.firebase.model;

import com.omitsis.firebasechat.firebase.interfaces.UsersDetailsInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Base user model that must be extended
 * In case you want to expand user details with your data model you may have to extend and implement
 * setUserHashMapForDB method in order to provide Firebase with the required data model and your expanded
 * data model
 */
public class UsersDetails implements UsersDetailsInterface {

    private String email;
    private String username;
    private String key;
    private boolean online;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * This method might be called before trying to sign Up
     * Sets the Map that Firebase needs to create a data structure on database
     *
     * @return Map to give as a param when signing up
     */
    @Override
    public Map<String, Object> setUserHashMapForDB() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", getEmail());
        map.put("username", getUsername());
        map.put("online", isOnline());
        return map;
    }
}
