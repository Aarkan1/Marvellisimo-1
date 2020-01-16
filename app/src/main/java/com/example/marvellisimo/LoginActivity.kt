package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login_login.setOnClickListener{
            preformLogin()
        }

        textView_login.setOnClickListener {
            finish()
        }
    }

    private fun preformLogin(){

        val email = editText_login_email.text.toString()
        val password = editText_login_password.text.toString()

        Log.d(TAG,"Email is: $email")
        Log.d(TAG,"Password is: $password")

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Enter email or password", Toast.LENGTH_SHORT).show()
        }
    }


}
