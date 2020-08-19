package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_scores.*

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        val g = intent.getSerializableExtra(GRADES) as Grades?
        val categories = g?.categories
        setNextCategory(categories)
    }

    // Set layout element currentCategory
    private fun setNextCategory(categories: ListIterator<String>?): Boolean {
        val hasNext = categories?.hasNext() == true
        if (hasNext) currentCategory.text = categories!!.next()
        return hasNext
    }
}