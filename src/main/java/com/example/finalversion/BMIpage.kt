package com.example.finalversion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class BMIpage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmipage2)

        val heightInput = findViewById<TextView>(R.id.bmiheight)
        val weightInput = findViewById<TextView>(R.id.bmiweight)
        val runBMI = findViewById<Button>(R.id.runbmi)
        val result = findViewById<TextView>(R.id.bmiresult)

        runBMI.setOnClickListener {
            val enteredHeight = heightInput.text.toString().toDoubleOrNull()
            val enteredWeight = weightInput.text.toString().toDoubleOrNull()

            if (enteredHeight == null || enteredWeight == null) {
                Toast.makeText(this@BMIpage, "Fill up the fields", Toast.LENGTH_SHORT).show()
            } else {
                val heightInMeters = enteredHeight / 100
                val bmi = enteredWeight / (heightInMeters * heightInMeters)

                result.text = "Your BMI is: %.2f".format(bmi)
            }
        }
    }
}