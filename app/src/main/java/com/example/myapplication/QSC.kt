package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.widget.Spinner
import android.widget.Toast
import org.json.JSONArray

class NewActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val uciFilesMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        // Add any necessary logic or setup for the new activity

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val storedIP = sharedPreferences.getString("IP", "")

        // Perform the HTTP GET request
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://$storedIP/api/v0/systems/1/ucis")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val response = bufferedReader.readText()
                    bufferedReader.close()

                    // Process the response here
                    runOnUiThread {
                        processResponse(response)
                    }
                } else {
                    Log.e("NewActivity", "HTTP GET request failed. Response Code: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("NewActivity", "Error performing HTTP GET request: ${e.message}", e)
            }
        }

        val confirmButton = findViewById<Button>(R.id.uciConfirm)
        confirmButton.setOnClickListener {
            val selectedOption = findViewById<Spinner>(R.id.uciOptions).selectedItem as? String
            val fileName = uciFilesMap[selectedOption]
            if (fileName != null) {

                //Log.d("NewActivity", "Selected Option: $selectedOption, File Name: $fileName")
                val intent = Intent(this, Browser::class.java)
                val extras = Bundle()
                extras.putString("fileName", fileName)
                extras.putString("selectedOption", selectedOption)
                intent.putExtras(extras)
                Thread {
                    startActivity(intent)
                }.start()

            } else {
                Log.d("NewActivity", "Invalid selection")
            }

        }

    }

    private fun processResponse(response: String) {
        val spinner = findViewById<Spinner>(R.id.uciOptions)

        // Parse the response (assuming it's in JSON format)
        val jsonArray = JSONArray(response)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val fileName = jsonObject.getString("fileName")
            uciFilesMap[name] = fileName
        }

        // Create an ArrayAdapter with the keys of the uciFilesMap
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, uciFilesMap.keys.toList())

        // Set the adapter to the Spinner
        spinner.adapter = adapter
    }
}
