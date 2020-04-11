package com.example.stockcar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class informationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        var intent = intent
        val code = intent.getStringExtra("Code")

        val resultTv = findViewById<TextView>(R.id.resultTv)
        resultTv.text = code

        val backBtn = findViewById<ImageButton>(R.id.imageButtonBack)
        backBtn.setOnClickListener {
            val intent = Intent(this@informationActivity,MainActivity::class.java)
            startActivity(intent)
        }
    }
}
