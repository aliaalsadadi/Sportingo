package org.tensorflow.lite.examples.poseestimation

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.loginActivity
import com.example.namespace.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.get
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.client.request.url
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeoutException


class ForgotActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var email: EditText
    private lateinit var resetBtn: Button
    private lateinit var backtoLogin: Button
    private val httpClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private fun hashString(inputString: String): String {
        val sha256Hash = MessageDigest.getInstance("SHA-256")
        val hashedBytes = sha256Hash.digest(inputString.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
    private fun randomID(): Int{
        return (0..100000).random()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        email = findViewById(R.id.email)
        resetBtn = findViewById(R.id.resetBtn)
        resetBtn.setOnClickListener{
            if (!Utils.isInternetAvailable(baseContext)){
                Toast.makeText(
                    baseContext,
                    "no Internet Connection",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }
            val emailText = email.text.toString().trim()
            if (TextUtils.isEmpty(emailText)){
                email.setError("Email is Required.")
                return@setOnClickListener
            }
            else if (!Utils.isValidEmail(emailText)){
                email.setError("Email is invalid")
                return@setOnClickListener
            }
            else{
                val id = randomID()
                val hashedID = hashString(id.toString())
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentTime = dateFormat.format(Date())
                val data = hashMapOf(
                    "hash" to hashedID,
                    "time" to currentTime
                )
                db.collection("forgotPasswd").document(emailText).set(data)
                sendGet(id,emailText)
                Toast.makeText(
                    baseContext,
                    "reset email sent to $emailText",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        backtoLogin = findViewById(R.id.backtoLogin)
        backtoLogin.setOnClickListener{
            val intent = Intent(this@ForgotActivity, loginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun sendGet(id: Int, email: String) {
        val url = URL("http://34.228.214.179/email/$email/$id")
        GetTask().execute(url)
    }

    private class GetTask : AsyncTask<URL, Void, String>() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun doInBackground(vararg urls: URL): String {
            val url = urls[0]
            return try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            println(line)
                        }
                    }
                    responseCode.toString()  // You may return any relevant result here
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in doInBackground: ${e.message}", e)
                "Error"  // You may handle errors as needed
            }
        }


    }
}

