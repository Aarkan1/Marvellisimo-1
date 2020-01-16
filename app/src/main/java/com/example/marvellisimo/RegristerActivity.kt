package com.example.marvellisimo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_regrister.*

private const val TAG = "RegristerActivity"

class RegristerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regrister)

        btn_regrister.setOnClickListener {
            preformRegrister()
        }

        textView_regrister.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun preformRegrister() {
        val username = editText_regrister_username.text.toString()
        val email = editText_regrister_email.text.toString()
        val password = editText_regrister_password.text.toString()

        Log.d(TAG, "Username is: $username")
        Log.d(TAG, "Email is: $email")
        Log.d(TAG, "Password is: $password")

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Enter username email, or password", Toast.LENGTH_SHORT).show()
            return
        }
    }
}
