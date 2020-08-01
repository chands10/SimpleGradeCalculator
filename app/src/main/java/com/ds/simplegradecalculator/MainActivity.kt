package com.ds.simplegradecalculator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val cats = mutableListOf(true)
    private var adapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = CategoryAdapter(this, cats)
        categories_list.adapter = adapter
    }

    fun addCategory(view: View) {
        cats.add(true)
        adapter?.notifyDataSetChanged()
    }

    fun createGrades(view: View) {
        TODO()
//        for (i in 0 until (categories_list.adapter?.itemCount ?: 0)) {
//
//        }
//        val category = editTextCategory0
//        val weight = editTextWeight0
//        if (checkGrade(category, weight)) {
//            val rawGrades = mapOf(category.text.toString() to weight.text.toString().toDouble())
//            try {
//                val g = Grades(rawGrades, getString(R.string.weightingError))
//            } catch (e: RuntimeException) {
//                weight.error = e.message
//            }
//        }
    }

    private fun checkGrade(category: EditText, weight: EditText): Boolean {
        if (category.text.toString().isBlank()) {
            category.error = getString(R.string.blank_field)
            return false
        } else if (weight.text.toString().isBlank()) {
            weight.error = getString(R.string.blank_field)
            return false
        }
        return true
    }

    private class CategoryAdapter(context: Context, cats: MutableList<Boolean>): ArrayAdapter<Boolean>(context, 0, cats) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            if (convertView == null) {
                return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            }
            return convertView
        }

    }
}