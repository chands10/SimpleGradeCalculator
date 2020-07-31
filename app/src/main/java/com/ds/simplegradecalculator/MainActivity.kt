package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createGrades(view: View) {
        val category = editTextCategory0
        val weight = editTextWeight0
        if (checkGrade(category, weight)) {
            try {
                val g = Grades(mapOf(category.text.toString() to weight.text.toString().toDouble()), getString(R.string.weightingError))
            } catch (e: RuntimeException) {
                weight.error = e.message
            }
        }
    }

    private fun checkGrade(category: EditText, weight: EditText): Boolean {
        if (category.text.toString().isBlank()) {
            category.error = "This field cannot be blank"
            return false
        } else if (weight.text.toString().isBlank()) {
            weight.error = "This field cannot be blank"
            return false
        }
        return true
    }
}