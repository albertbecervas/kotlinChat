package com.omitsis.ktlnchat.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.omitsis.firebasechat.firebase.FirebaseUsersList
import com.omitsis.firebasechat.firebase.model.UsersDetails
import com.omitsis.ktlnchat.R
import com.omitsis.ktlnchat.model.UserData
import com.omitsis.ktlnchat.model.Users
import com.omitsis.ktlnchat.ui.LoginActivity
import kotlinx.android.synthetic.main.item_user_layout.view.*

class UsersAdapter(val context: Context, val users: Users) : RecyclerView.Adapter<UsersAdapter.usersViewHolder>() {
    override fun onBindViewHolder(holder: usersViewHolder?, position: Int) {
        val user: UsersDetails = users.usersList[position]
        holder?.bind(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): usersViewHolder =
            usersViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false), context)


    override fun getItemCount(): Int = users.usersList.size


    class usersViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bind(user: UsersDetails) {
            with(user) {
                itemView.name.text = username
                itemView.setOnClickListener { context.startActivity(Intent(context, LoginActivity::class.java)) }
                itemView.setOnLongClickListener {
                    Toast.makeText(context,"selected", Toast.LENGTH_SHORT).show()
                    itemView.setBackgroundColor(R.color.colorAccent)
                    FirebaseUsersList(context,UserData.name).removeUser(key)
                    true
                }
            }
        }
    }
}