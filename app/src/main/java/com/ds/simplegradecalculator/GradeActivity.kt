package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import kotlinx.android.synthetic.main.activity_grade.*

class GradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)

        val g = intent.getSerializableExtra(GRADES) as Grades?
        if (g != null) {
            val gradeText = "%.2f".format(g.calculateGrade())
            val fadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 1000 }
            gradeTextView.apply {
                text = gradeText
                startAnimation(fadeIn)
            }
        }
    }
}