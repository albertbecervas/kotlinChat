package com.omitsis.ktlnchat.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.omitsis.firebasechat.firebase.FirebaseUsersList
import com.omitsis.firebasechat.firebase.UsersCallback
import com.omitsis.firebasechat.firebase.helper.FirebaseChatHelper
import com.omitsis.firebasechat.firebase.model.UsersDetails
import com.omitsis.ktlnchat.R
import com.omitsis.ktlnchat.adapter.UsersAdapter
import com.omitsis.ktlnchat.model.UserData
import com.omitsis.ktlnchat.model.Users
import kotlinx.android.synthetic.main.activity_users.*
import java.util.*

class UsersActivity : AppCompatActivity(), UsersCallback {

    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        mRecyclerView = usersRecyclerView
        val mLinearLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLinearLayoutManager

        val firebaseUsers = FirebaseUsersList(this,UserData.name)

        firebaseUsers.getUsers()

        val mDatabase = FirebaseDatabase.getInstance().reference
        val firebaseHelper = FirebaseChatHelper(this, mDatabase)
        firebaseHelper.setUserStatusOnline("true")

    }

    override fun onUsersLoaded(usersDetails: ArrayList<UsersDetails>?, totalUsers: Int, dataSnapshot: DataSnapshot?) {
        val users = Users(usersDetails!!)
        val mAdapter = UsersAdapter(this, users)
        mRecyclerView.adapter = mAdapter
    }

    override fun onLoadUserDetailsExpansion(dataSnapshot: DataSnapshot?) {
    }
}
