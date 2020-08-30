package com.ds.simplegradecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import kotlinx.android.synthetic.main.activity_grade.*

class GradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)

        val g = intent.getSerializableExtra(GRADES) as Grades?
        if (g != null) {
            val gradeText = "%.2f".format(g.calculateGrade())
            gradeTextView.text = gradeText

            val fadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 1000 }
            val fadeIn2 = AlphaAnimation(0.0f, 1.0f).apply {
                duration = 1000
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                        resetButton.apply {
                            visibility = Button.VISIBLE
                            isClickable = false
                        }
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        resetButton.isClickable = true
                    }

                    override fun onAnimationRepeat(p0: Animation?) = onAnimationStart(p0)
                })
            }

            fadeIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    resetButton.apply {
                        visibility = Button.INVISIBLE
                        isClickable = false
                    }
                }
                override fun onAnimationEnd(p0: Animation?) = resetButton.startAnimation(fadeIn2)
                override fun onAnimationRepeat(p0: Animation?) {}
            })
            gradeTextView.startAnimation(fadeIn)
        }
    }

    // Clear all data and start over, reset back stack
    fun reset(view: View) {
        intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
}