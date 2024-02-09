package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.namespace.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.tensorflow.lite.examples.poseestimation.Utils


class PodiumFragment : Fragment() {
    private var db = Firebase.firestore
    private lateinit var table: TableLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val auth = Firebase.auth
    private val userDoc = db.collection("users").document(auth.currentUser?.email.toString())

    private fun cleanTable(table: TableLayout) {
        val childCount = table.childCount

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1)
        }
    }
    private fun makeRow(names: MutableList<String>,  pushupsList: MutableList<String>, situpsList: MutableList<String> , totalList: MutableList<Int>, username: String): ArrayList<TableRow> {
        var array = arrayListOf<TableRow>()

        for (i in names.indices){
            val row = TableRow(requireContext())
            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            row.layoutParams = layoutParams
            val name = TextView(requireContext())
            name.text = names[i]
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            name.gravity = Gravity.CENTER
            name.textSize = 15F
            name.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            row.addView(name)


            val pushups = TextView(requireContext())
            pushups.text = pushupsList[i]
            pushups.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            pushups.gravity = Gravity.CENTER
            pushups.textSize = 15F
            pushups.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            row.addView(pushups)

            val situps = TextView(requireContext())
            situps.text = situpsList[i]
            situps.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            situps.gravity = Gravity.CENTER
            situps.textSize = 15F
            situps.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            row.addView(situps)

            val total = TextView(requireContext())
            total.text = totalList[i].toString()
            total.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            total.gravity = Gravity.CENTER
            total.textSize = 15F
            total.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            row.addView(total)

            if (names[i] == username)
            {
                row.setBackgroundResource(R.drawable.rossena)
                name.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                pushups.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                situps.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                total.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                name.text = "YOU"
            }

            array.add(row)
        }
        return array
    }

    private fun UpdataTableData(){
        cleanTable(table)
        userDoc.get()
            .addOnSuccessListener { document ->
                if (document == null) Log.e(TAG, "no such doc")
                val username= document.getString("user")!!
                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        var names = mutableListOf<String>()
                        var pushupsList = mutableListOf<String>()
                        var situpsList = mutableListOf<String>()
                        var totalList = mutableListOf<Int>()


                        for (document in result) {
                            names.add(document.getString("user")!!)
                            val pushups = document.get("pushups").toString().toIntOrNull() ?: 0 // Check if the value is a valid integer
                            val situps = document.get("situps").toString().toIntOrNull() ?: 0 // Check if the value is a valid integer
                            pushupsList.add(pushups.toString())
                            situpsList.add(situps.toString())
                            totalList.add(pushups + situps)
                        }



                        val indexList = totalList.indices.toList()

                        val comparator = compareByDescending<Int> { totalList[it] } // Use compareByDescending instead of compareBy
                        val sortedIndices = indexList.sortedWith(comparator)

                        names = sortedIndices.map { names[it] }.toMutableList()
                        pushupsList = sortedIndices.map { pushupsList[it] }.toMutableList()
                        situpsList = sortedIndices.map { situpsList[it] }.toMutableList()
                        totalList = sortedIndices.map { totalList[it] }.toMutableList()
                        val arrRows = makeRow(names, pushupsList, situpsList, totalList, username)
                        for (row in arrRows) {
                            table.addView(row)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Error getting documents: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_podium, container, false)
        table = view.findViewById(R.id.tablelayout)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            if (!Utils.isInternetAvailable(view.context))
            {
                Toast.makeText(view.context, "No interntet connection", Toast.LENGTH_SHORT).show()
            }else{
                UpdataTableData()
                swipeRefreshLayout.isRefreshing = false
            }
        }
        UpdataTableData()


        return view
    }
}