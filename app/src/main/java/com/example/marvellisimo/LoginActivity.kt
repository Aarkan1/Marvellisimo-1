package com.example.marvellisimo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.marvellisimo.repository.DB
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

private const val TAG = "LoginActivity"

class LoginActivity: AppCompatActivity() {

    @Inject
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        MarvellisimoApplication.applicationComponent.inject(this)

        btn_login_login.setOnClickListener{
            preformLogin()
        }

        textView_login.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun preformLogin(){
        val email = editText_login_email.text.toString()
        val password = editText_login_password.text.toString()

        val credential = UserPasswordCredential(email, password)
        DB.stitchClient.auth.loginWithCredential(credential)
            .addOnCompleteListener {
                if(!it.isSuccessful)return@addOnCompleteListener
                Log.d(TAG, "Successfully logged in as user " + it.result?.id)

                viewModel.updateLoggedInUser()

                val intent = Intent(this, MainActivity::class.java)
                // reset activity stack/history
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                  Log.d(TAG, "Failure logging in: " + it.message)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Enter email or password", Toast.LENGTH_SHORT).show()
        }
    }


}
