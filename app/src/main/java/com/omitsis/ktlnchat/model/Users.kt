package com.omitsis.ktlnchat.model

import com.omitsis.firebasechat.firebase.model.UsersDetails

data class Users(val usersList: ArrayList<UsersDetails>)

data class User(val name : String, val image: String, val admin: Boolean) : UsersDetails()
