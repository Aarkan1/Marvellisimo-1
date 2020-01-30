package com.example.marvellisimo.activity.login

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.marvellisimo.*
import com.example.marvellisimo.activity.main.MainActivity
import com.example.marvellisimo.activity.register.RegisterActivity
import com.example.marvellisimo.repository.DB
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
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
            preformLogin()
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

    private fun preformLogin() {
        val email = editText_login_email.text.toString()
        val password = editText_login_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "You need to enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Main).launch { loadingDialog.show() }

        val credential = UserPasswordCredential(email, password)
        DB.stitchClient.auth.loginWithCredential(credential)
            .addOnCompleteListener {
                CoroutineScope(Main).launch { loadingDialog.dismiss() }
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d(TAG, "Successfully logged in as user " + it.result?.id)

                viewModel.updateLoggedInUser()

                val intent = Intent(this, MainActivity::class.java)
                // reset activity stack/history
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                CoroutineScope(Main).launch { loadingDialog.dismiss() }
                Log.d(TAG, "Failure logging in: " + it.message)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }


}
