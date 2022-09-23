package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val titleTextView: TextView = findViewById(R.id.title_text_view)
        val downloadStatusTextView: TextView = findViewById(R.id.download_status_text_view)

        val extras: Bundle? = intent.extras


        if (extras != null) {
            if (extras.containsKey("title")) {
                val title: String? = extras.getString("title")
                titleTextView.text = title
            }
            if (extras.containsKey("status")) {
                val status: String? = extras.getString("status")
                if (status == "Success") {
                    downloadStatusTextView.setTextColor(Color.GREEN)
                }
                else {
                    downloadStatusTextView.setTextColor(Color.RED)
                }
                downloadStatusTextView.text = status
            }
        }

        button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
