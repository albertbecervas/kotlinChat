package com.omitsis.ktlnchat.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.omitsis.firebasechat.firebase.FirebaseLogin
import com.omitsis.firebasechat.firebase.LoginCallback
import com.omitsis.firebasechat.firebase.helper.FirebaseChatHelper
import com.omitsis.firebasechat.firebase.model.ChatDetails
import com.omitsis.ktlnchat.R
import com.omitsis.ktlnchat.model.UserData
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*


class LoginActivity : AppCompatActivity(), LoginCallback {

    lateinit var progressDialog: ProgressDialog
    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)

        val firebaseLogin = FirebaseLogin(this)
        mDatabase = FirebaseDatabase.getInstance().reference

        loginButton.setOnClickListener {
            progressDialog.setMessage("Signing In...")
            progressDialog.show()
            val mail = email.text.toString()
            val pass = password.text.toString()
            firebaseLogin.signInAuthUserWithEmailAndPassword(mail, pass, mDatabase)
        }


        val intent = Intent(this, SignUpActivity::class.java)
        signUp.setOnClickListener {
            startActivity(intent)
            finish()
        }
    }

    override fun onLoginSucced(chats: ArrayList<ChatDetails>?, username: String?, userId: String?, isAdmin: Boolean, dataSnapshot: DataSnapshot?) {
        if (progressDialog.isShowing) progressDialog.dismiss()
        UserData.key = userId
        UserData.name = username

        val intent: Intent = if(isAdmin) Intent(this, UsersActivity::class.java) else Intent(this, UsersActivity::class.java)
        startActivity(intent)
    }

    override fun onLoginFailed(error: String?) {
        toast("error user auth")
    }
}
