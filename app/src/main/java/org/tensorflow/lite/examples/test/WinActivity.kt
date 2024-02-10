package org.tensorflow.lite.examples.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.AppActivity
import com.example.namespace.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WinActivity : AppCompatActivity() {
    private lateinit var matchId: String
    private lateinit var winnerText: TextView
    private lateinit var byText : TextView
    private lateinit var okBtn: Button
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)
        matchId = intent.getStringExtra("id")!!
        byText = findViewById(R.id.margin)
        winnerText = findViewById(R.id.winner)
        okBtn = findViewById(R.id.ok)
        okBtn.setOnClickListener {
            var newIntent = Intent(this@WinActivity, AppActivity::class.java)
            startActivity(newIntent)
            finish()
        }
        db.collection("matches").document(matchId).get().addOnSuccessListener { document ->
            val p1 = document.getString("p1")
            val p2 = document.getString("p2")
            val p1Points = document.get("p1Points").toString().toDouble().toInt()
            val p2Points = document.get("p2Points").toString().toDouble().toInt()
            if (p1Points>p2Points){
                winnerText.text = p1
                var tmp = (p1Points-p2Points).toString() + " pushups"
                byText.text = tmp
            }
            else if (p2Points>p1Points){
                winnerText.text = p2
                var tmp = (p2Points-p1Points).toString() + " pushups"
                byText.text = tmp
            }
            else{
                winnerText.text = "DRAW!"
            }
        }

    }
}
