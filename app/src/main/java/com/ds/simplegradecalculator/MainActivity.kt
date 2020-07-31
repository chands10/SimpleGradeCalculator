package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createGrades(view: View) {
        val c = editTextCategory0.text.toString()
        val w = editTextWeight0.text.toString().toDoubleOrNull()
        TODO("Add error/type checking")
//        val g = Grades(mapOf(c to w), getString(R.string.weightingError))
    }
}