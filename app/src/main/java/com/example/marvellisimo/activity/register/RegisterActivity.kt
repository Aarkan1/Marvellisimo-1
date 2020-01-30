package com.example.marvellisimo.activity.register

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.marvellisimo.MainActivity
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.login.LoginActivity
import com.example.marvellisimo.repository.DB
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.bson.Document
import org.bson.types.ObjectId
import javax.inject.Inject

private const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: RegisterViewModel

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        MarvellisimoApplication.applicationComponent.inject(this)

        createLoadingDialog()

        btn_regrister.setOnClickListener {
            preformRegister()
        }

        textView_regrister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
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

    private fun preformRegister() {
        val username = editText_regrister_username.text.toString()
        val email = editText_regrister_email.text.toString()
        val password = editText_regrister_password.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please enter username email and password", Toast.LENGTH_SHORT).show()
        }

        val emailPassClient = Stitch.getDefaultAppClient().auth.getProviderClient(
            UserPasswordAuthProviderClient.factory
        )

        CoroutineScope(Main).launch { loadingDialog.show() }

        emailPassClient.registerWithEmail(email, password)
            .addOnSuccessListener {
                val credential = UserPasswordCredential(email, password)
                // auto login after sign up
                // because mongoDB Stitch requires confirmation + sign in to get a _id
                DB.stitchClient.auth.loginWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Log.d("stitch", "Successfully registered user: ${task.result!!.id}")
                            val userDoc = Document()
                            userDoc["_id"] = ObjectId(task.result!!.id)
                            userDoc["uid"] = task.result!!.id
                            userDoc["username"] = username
                            userDoc["email"] = email
                            userDoc["avatar"] = ""
                            userDoc["isOnline"] = true
                            userDoc["favoriteSeries"] = ArrayList<String>()
                            userDoc["favoriteCharacters"] = ArrayList<String>()
                            viewModel.createNewUser(userDoc)

                            CoroutineScope(Main).launch { loadingDialog.dismiss() }
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error registering new user:", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        CoroutineScope(Main).launch { loadingDialog.dismiss() }
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                CoroutineScope(Main).launch { loadingDialog.dismiss() }
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
