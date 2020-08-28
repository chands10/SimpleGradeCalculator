package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_grade.*

class GradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)

        val g = intent.getSerializableExtra(GRADES) as Grades?
        if (g != null) {
            val gradeText = "%.2f".format(g.calculateGrade())
            gradeTextView.text = gradeText
        }
    }
}