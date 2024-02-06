package com.example.myapplication
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.material.icons.materialIcon
import androidx.fragment.app.Fragment
import com.example.namespace.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.tensorflow.lite.examples.poseestimation.RaceActivity
import org.tensorflow.lite.examples.poseestimation.matchActivity


class ProfileFragment : Fragment() {
    private var db = Firebase.firestore
    private lateinit var pushupsText: TextView
    private lateinit var situps: TextView
    private lateinit var raceButton: AppCompatButton
    private lateinit var logoutBtn: AppCompatButton
    private val auth = Firebase.auth
    private lateinit var user: String
    private val userDoc = db.collection("users").document(auth.currentUser?.email.toString())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        userDoc.get().addOnSuccessListener { document ->
            user = document.getString("user")!!
        }
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val username = view.findViewById<TextView>(R.id.username)
        raceButton = view.findViewById(R.id.race)
        raceButton.setOnClickListener {
            // Check if there is an ongoing match with an available space for player 2
            db.collection("matches")
                .whereEqualTo("status", "ongoing")
                .whereEqualTo("p2", null)
                .get()
                .addOnSuccessListener { matches ->
                    if (matches.isEmpty) {
                        // No ongoing match with an available space, create a new match
                        createNewMatch(user)
                    } else {
                        // Found an ongoing match with an available space, join that match
                        joinExistingMatch(matches.documents[0].id,user)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error checking for ongoing match: $exception")
                }
        }
        pushupsText = view.findViewById(R.id.pushups)
        situps = view.findViewById(R.id.situps)
        val userDoc = db.collection("users").document(auth.currentUser?.email.toString())
        userDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val usernameValue = document.getString("user")
                    username.setText(usernameValue)
                    pushupsText.setText(document.get("pushups").toString())
                    situps.setText(document.get("situps").toString())
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        logoutBtn = view.findViewById<AppCompatButton>(R.id.logout)
        logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), loginActivity::class.java)
            startActivity(intent)
        }
        return view
    }


    private fun createNewMatch(currentPlayerEmail: String) {
        // Create a new match document
        val newMatch = hashMapOf(
            "p1" to currentPlayerEmail,
            "p2" to null,
            "status" to "ongoing",
            "p1Points" to 0,
            "p2Points" to 0
        )

        // Add the new match to the "matches" collection
        db.collection("matches")
            .add(newMatch)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "New match created with ID: ${documentReference.id}")
                val intent = Intent(requireContext(), matchActivity::class.java)
                intent.putExtra("id", documentReference.id)
                intent.putExtra("isP1", true)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun joinExistingMatch(matchId: String, currentPlayerEmail: String) {
        // Update the existing match document with the current user as player 2
        db.collection("matches").document(matchId)
            .update("p2", currentPlayerEmail)
            .addOnSuccessListener {
                Log.d(TAG, "Joined existing match with ID: $matchId")
                var intent = Intent(requireContext(), RaceActivity::class.java)
                intent.putExtra("id", matchId)
                intent.putExtra("isP1",false)
                startActivity(intent)

                // Update the UI or navigate to the ongoing match as needed
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

}
