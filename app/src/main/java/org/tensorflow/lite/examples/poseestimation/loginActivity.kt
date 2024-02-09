package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.namespace.R
import com.google.firebase.Firebase
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import org.tensorflow.lite.examples.poseestimation.ForgotActivity
import org.tensorflow.lite.examples.poseestimation.Utils

class loginActivity : ComponentActivity() {
    private lateinit var email: EditText
    private lateinit var passwd: EditText
    private lateinit var buttonLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var registerBtn: Button
    private var incorrectCount = 0
    private val db = Firebase.firestore
    private lateinit var forgotPassword: TextView
    private var loginEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.email)
        forgotPassword = findViewById(R.id.forgotPasswd)
        passwd = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.btn_login)
        registerBtn = findViewById(R.id.registerNow)
        registerBtn.setOnClickListener{
            val intent: Intent = Intent(this@loginActivity, Register::class.java)
            startActivity(intent)
            finish()
        }
        forgotPassword.setOnClickListener{
            val intent: Intent = Intent(this@loginActivity, ForgotActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonLogin.setOnClickListener(View.OnClickListener {
            if (!Utils.isInternetAvailable(baseContext)){
                Toast.makeText(
                    baseContext,
                    "no Internet Connection",
                    Toast.LENGTH_SHORT,
                ).show()
                return@OnClickListener
            }
            var user : String = email.text.toString().trim()
            var password : String = passwd.text.toString().trim()
            if (TextUtils.isEmpty(user)){
                email.setError("Email is Required.")
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)){
                passwd.setError("Password Required")
                return@OnClickListener
            }
            if (!Utils.isValidEmail(user)){
                email.setError("enter a valid email")
                return@OnClickListener
            }
            if (loginEnabled) {
                auth = Firebase.auth
                db.collection("users").document()
                auth.signInWithEmailAndPassword(user, password)
                    .addOnCompleteListener(this) { task ->
                        when {
                            task.isSuccessful -> {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@loginActivity, AppActivity::class.java)
                                startActivity(intent)
                            }
                            task.exception is FirebaseTooManyRequestsException -> {
                                Toast.makeText(applicationContext, "Too many failed login attempts", Toast.LENGTH_SHORT).show()
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(baseContext, "Invalid email or password.", Toast.LENGTH_SHORT).show()
                                incorrectCount++
                                if (incorrectCount >= 3) {
                                    loginEnabled = false
                                    Handler().postDelayed({
                                        loginEnabled = true
                                        incorrectCount = 0
                                    }, 30000) // 30 seconds delay
                                }
                            }
                            task.exception is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(baseContext, "Invalid User", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(baseContext, "Login is temporarily disabled. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

