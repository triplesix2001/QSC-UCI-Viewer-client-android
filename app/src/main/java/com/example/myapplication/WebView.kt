package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Browser : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        supportActionBar?.hide()

        webView = findViewById(R.id.Web)
        webView.settings.domStorageEnabled = true

        // Request runtime permission for INTERNET if not already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                1
            )
        }

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val storedIP = sharedPreferences.getString("IP", "")

        val extras = intent.extras
        val fileName = extras?.getString("fileName")
        val selectedOption = extras?.getString("selectedOption")

        if (fileName != null && selectedOption != null) {
            Log.d("WebView", "File Name: $fileName, Option: $selectedOption")

            val url = "http://$storedIP/uci-viewer/?uci=$selectedOption&file=$fileName&directory=/designs/current_design/UCIs/"
            Log.d("WebView", "URL: $url")
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        } else {
            Log.d("WebView", "Invalid extras")
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
