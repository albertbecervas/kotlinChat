package com.omitsis.firebasechat.firebase;

import com.google.firebase.database.DataSnapshot;
import com.omitsis.firebasechat.firebase.model.UsersDetails;

import java.util.ArrayList;

public interface UsersCallback {

    void onUsersLoaded(ArrayList<UsersDetails> users, int totalUsers, DataSnapshot dataSnapshot);

    void onLoadUserDetailsExpansion(DataSnapshot dataSnapshot);

}
