package com.example.marvellisimo.activity.login

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.marvellisimo.*
import com.example.marvellisimo.activity.register.RegisterActivity
import com.example.marvellisimo.repository.DB
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import javax.inject.Inject

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: LoginViewModel
    lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        MarvellisimoApplication.applicationComponent.inject(this)

        createLoadingDialog()

        btn_login_login.setOnClickListener {
            CoroutineScope(Main).launch { loadingDialog.show() }
            runBlocking {
                preformLogin()
            }
        }

        textView_login.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.progressDialog_message)
        message.text = getString(R.string.loading_dialog_text)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()
    }

    private suspend fun preformLogin() {
        val email = editText_login_email.text.toString()
        val password = editText_login_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "You need to enter both email and password", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val credential = UserPasswordCredential(email, password)
        val task = DB.stitchClient.auth.loginWithCredential(credential)
        var timeout = 0L
        try {
            while (!task.isComplete) delay(5)
            timeout += 5L
            if (timeout >= 10000L) throw Exception("Login Timeout")
            Log.d(TAG, "Successfully logged in as user " + task.result?.id)
        } catch (e: Exception) {
            CoroutineScope(Main).launch { loadingDialog.dismiss() }
            Log.e(TAG, "Failure logging in: " + e.message)
            Toast.makeText(this, "Failed to login, try again", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Main).launch { loadingDialog.dismiss() }
        viewModel.updateLoggedInUser()

        val intent = Intent(this, MainActivity::class.java)
        // reset activity stack/history
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
