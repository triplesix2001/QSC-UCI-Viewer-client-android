package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editText: EditText

    companion object {
        private const val INTERNET_REQUEST_CODE = 100
        private const val BLUETOOTH_REQUEST_CODE = 101
        private const val STORAGE_REQUEST_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        editText = findViewById(R.id.editIP)
        val button = findViewById<Button>(R.id.confirmIP)

        // Check and request permissions
        checkAndRequestPermissions()

        // Retrieve the stored IP, or an empty string if it's not found
        val storedIP = sharedPreferences.getString("IP", "")

        // Set the retrieved IP to the EditText
        editText.setText(storedIP)

        button.setOnClickListener {
            val inputText = editText.text.toString()
            Log.d("MainActivity", "Button clicked. Input text: $inputText")

            // Store the IP in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("IP", inputText)
            editor.apply()

            // Start the NewActivity
            val intent = Intent(this, NewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAndRequestPermissions() {
        val internetPermission = Manifest.permission.INTERNET
        val bluetoothPermission = Manifest.permission.BLUETOOTH
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, internetPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(internetPermission)
        }

        if (ContextCompat.checkSelfPermission(this, bluetoothPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(bluetoothPermission)
        }

        if (ContextCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(storagePermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), INTERNET_REQUEST_CODE)
        }
    }
}


