package com.example.marvellisimo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential
import kotlinx.android.synthetic.main.activity_register.*
import org.bson.Document
import org.bson.types.ObjectId

private const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_regrister.setOnClickListener {
            preformRegister()
        }

        textView_regrister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun preformRegister() {
        val username = editText_regrister_username.text.toString()
        val email = editText_regrister_email.text.toString()
        val password = editText_regrister_password.text.toString()

        val emailPassClient = Stitch.getDefaultAppClient().auth.getProviderClient(
            UserPasswordAuthProviderClient.factory
        )

        emailPassClient.registerWithEmail(email, password)
            .addOnSuccessListener {
                val credential = UserPasswordCredential(email, password)
                // auto login after sign up
                // because mongoDB Stitch requires confirmation + sign in to get a _id
                DB.client.auth.loginWithCredential(credential)
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
                            DB.users.insertOne(userDoc)
                            userDoc["favoriteSeries"] = ArrayList<String>()
                            userDoc["favoriteCharacters"] = ArrayList<String>()
                            DB.users.insertOne(userDoc)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error registering new user:", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Enter username email, or password", Toast.LENGTH_SHORT).show()
        }
    }
}
