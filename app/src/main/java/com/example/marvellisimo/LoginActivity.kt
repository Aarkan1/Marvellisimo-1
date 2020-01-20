package com.example.marvellisimo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential
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
            val intent = Intent(this, RegristerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun preformLogin(){

        val email = editText_login_email.text.toString()
        val password = editText_login_password.text.toString()

        val credential = UserPasswordCredential(email, password)
        DB.client.auth.loginWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("stitch", "Successfully logged in as user " + it.result?.id)

                    val intent = Intent(this, MainActivity::class.java)
                    // reset activity stack/history
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Log.e("stitch", "Error logging in with email/password auth:", it.exception)
                }
            }

        Log.d(TAG,"Email is: $email")
        Log.d(TAG,"Password is: $password")

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Enter email or password", Toast.LENGTH_SHORT).show()
        }
    }


}
