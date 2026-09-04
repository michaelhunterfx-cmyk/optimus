package com.optimus.iptv

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val container = findViewById<LinearLayout>(R.id.checkboxContainer)
        val saveButton = findViewById<Button>(R.id.saveSettingsButton)

        val prefs = getSharedPreferences("optimus_prefs", MODE_PRIVATE)
        val hiddenGroups = prefs.getStringSet("hidden_groups", emptySet()) ?: emptySet()

        val allGroups = intent.getStringArrayListExtra("all_groups") ?: arrayListOf()

        val checkBoxes = mutableListOf<CheckBox>()

        for (group in allGroups) {
            val checkBox = CheckBox(this)
            checkBox.text = group
            checkBox.setTextColor(resources.getColor(R.color.optimus_light, theme))
            checkBox.isChecked = !hiddenGroups.contains(group)
            container.addView(checkBox)
            checkBoxes.add(checkBox)
        }

        saveButton.setOnClickListener {
            val newHidden = mutableSetOf<String>()
            for (checkBox in checkBoxes) {
                if (!checkBox.isChecked) {
                    newHidden.add(checkBox.text.toString())
                }
            }
            prefs.edit().putStringSet("hidden_groups", newHidden).apply()
            finish()
        }
    }
}
