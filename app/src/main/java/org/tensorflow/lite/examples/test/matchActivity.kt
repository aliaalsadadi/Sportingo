package org.tensorflow.lite.examples.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.namespace.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class matchActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var matchId: String
    private lateinit var matchDoc: DocumentReference
    private lateinit var matchDocListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        matchId = intent.getStringExtra("id")!!
        matchDoc = db.collection("matches").document(matchId)

        // Set up a listener to observe changes in the "p2" field
        matchDocListener = matchDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle the error
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Check if p2 has arrived (you need to replace "p2" with your actual field name)
                val p2Arrived = snapshot.getString("p2") != null

                if (p2Arrived) {
                    // Switch to another activity when p2 has arrived
                    switchToAnotherActivity()
                }
            }
        }
    }

    private fun switchToAnotherActivity() {
        val intent = Intent(this@matchActivity, RaceActivity::class.java)
        intent.putExtra("id",matchId)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the listener when the activity is destroyed to avoid memory leaks
        matchDocListener.remove()
    }
}
