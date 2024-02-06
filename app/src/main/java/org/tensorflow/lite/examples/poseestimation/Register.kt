package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.namespace.R

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import org.tensorflow.lite.examples.poseestimation.Utils

class Register : ComponentActivity() {
    private lateinit var email: EditText
    private lateinit var passwd: EditText
    private lateinit var buttonReg: Button
    private lateinit var username: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var loginBtn:Button
    private val db = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        email = findViewById(R.id.email)
        username= findViewById(R.id.username)
        passwd = findViewById(R.id.password)
        loginBtn = findViewById(R.id.LoginNow)
        buttonReg = findViewById(R.id.btn_register)
        loginBtn.setOnClickListener {
            val intent = Intent(this@Register, loginActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonReg.setOnClickListener(View.OnClickListener {
            if (!Utils.isInternetAvailable(baseContext)){
                Toast.makeText(
                    baseContext,
                    "no Internet Connection",
                    Toast.LENGTH_SHORT,
                ).show()
                return@OnClickListener
            }
            var user : String = email.text.toString()
            println(user)
            Log.e(TAG,user)
            var password : String = passwd.text.toString().trim()
            println(password)
            Log.e(TAG, password)
            if (TextUtils.isEmpty(user)){
                email.setError("Email is Required.")
                return@OnClickListener
            }
            if (TextUtils.isEmpty(username.text.toString())){
                username.setError("username is Required")
                return@OnClickListener
            }
            if (username.text.length < 3){
                username.setError("username must > 3 characters")
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)){
                passwd.setError("Password Required")
                return@OnClickListener
            }
            if (password.length <= 6){
                passwd.setError("Password Must be >= 6 Characters")
                return@OnClickListener
            }
            db.collection("users").get()
                .addOnSuccessListener {querySnapshot ->
                    var taken = false
                    for (document in querySnapshot) {
                        if (document.get("user") == username.text.toString()) {
                            // User exists with the given username
                            // Perform your desired actions here
                            taken = true
                            Toast.makeText(
                                baseContext,
                                "UserName Taken",
                                Toast.LENGTH_SHORT
                            ).show()
                            break // Exit the loop since the user has been found
                        }
                    }
                    if (!taken)
                    {
                        createUser(user, password, username.text.toString())
                    }
                }

                .addOnFailureListener {exception ->
                    Log.e(TAG, "Error getting documents: ${exception.message}")
                }


        })
    }
    private fun createUser(user :String, password: String , username: String){
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(user, password)
            .addOnCompleteListener(this) { task ->
                try{
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(
                            baseContext,
                            "created account",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val user_Data = hashMapOf(
                            "user" to username,
                            "situps" to 0,
                            "pushups" to 0,
                        )
                        val db = Firebase.firestore
                        val users = db.collection("users")
                        users.document(user).set(user_Data)
                        val intent = Intent(this@Register, loginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        throw task.exception!!
                    }
                }
                catch (e : Exception){
                    when (e){
                        is FirebaseAuthUserCollisionException -> {
                            // Handle user collision exception
                            Log.e(TAG, "User with the same email already exists.")
                            Toast.makeText(
                                baseContext,
                                "User with the same email already exists.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // Handle other authentication exceptions
                            Log.e(TAG, "Authentication failed: ${e.message}")
                            Toast.makeText(
                                baseContext,
                                "Authentication failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }
    }
}
