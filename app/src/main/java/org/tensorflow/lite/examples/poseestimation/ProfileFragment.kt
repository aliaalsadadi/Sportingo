package com.example.myapplication
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.namespace.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private var db = Firebase.firestore
    private lateinit var curlsText : TextView
    private lateinit var pushupsText: TextView
    private lateinit var situps: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val auth = Firebase.auth
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val username = view.findViewById<TextView>(R.id.username)
        curlsText = view.findViewById(R.id.curls)
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
                    curlsText.setText(document.get("bicep").toString())
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        val logoutBtn = view.findViewById<AppCompatButton>(R.id.logout)
        logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), loginActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}
