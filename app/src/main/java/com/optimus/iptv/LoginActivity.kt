package com.optimus.iptv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs = getSharedPreferences("optimus_prefs", MODE_PRIVATE)

        val hostInput = findViewById<EditText>(R.id.hostInput)
        val userInput = findViewById<EditText>(R.id.userInput)
        val passInput = findViewById<EditText>(R.id.passInput)
        val saveButton = findViewById<Button>(R.id.saveButton)

        hostInput.setText(prefs.getString("host", ""))
        userInput.setText(prefs.getString("username", ""))
        passInput.setText(prefs.getString("password", ""))

        saveButton.setOnClickListener {
            val host = hostInput.text.toString().trim()
            val user = userInput.text.toString().trim()
            val pass = passInput.text.toString().trim()

            prefs.edit()
                .putString("host", host)
                .putString("username", user)
                .putString("password", pass)
                .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
