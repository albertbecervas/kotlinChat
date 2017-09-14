package com.omitsis.ktlnchat.ui

import android.content.Intent
import java.util.HashMap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.omitsis.firebasechat.firebase.FirebaseSignUp
import com.omitsis.firebasechat.firebase.SignupCallback
import com.omitsis.ktlnchat.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), SignupCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val mDatabase = FirebaseDatabase.getInstance().reference
        val firebaseSignUp = FirebaseSignUp(this, mDatabase)

        signupButton.setOnClickListener {
            val name = name.text.toString()
            val email = email.text.toString()
            val password = password.text.toString()

            val map = HashMap<String, Any>()
            if (adminSwitch.isChecked) {
                map.put("isAdmin", true)
            }
            map.put("username", name)

            firebaseSignUp.CreateAuthUserWithEmailAndPassword(map, email, password)
        }

        val intent = Intent(this, LoginActivity::class.java)
        signIn.setOnClickListener {
            startActivity(intent)
            finish()
        }

    }

    override fun onSignUpSucceed() {
    }

    override fun onSignUpFailed() {
    }
}

