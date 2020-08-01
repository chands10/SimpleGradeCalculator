package com.ds.simplegradecalculator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // used for Add Another button
    val cats = mutableListOf(true)
    private var adapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = CategoryAdapter(this, cats)
        categories_list.adapter = adapter
    }

    // Add Another button: adds another field to input text
    fun addCategory(view: View) {
        cats.add(true)
        adapter?.notifyDataSetChanged()
    }

    // Continue button: Verifies scores, creates Grades class, and continues onto the next page
    // TODO: Pass Grades g into next activity
    fun createGrades(view: View) {
        val rawGrades = mutableMapOf<String, Double>()
        for (i in 0 until cats.size) {
            val current = categories_list.getChildAt(i)
            val category = current.findViewById<EditText>(R.id.editTextCategory)
            val weight = current.findViewById<EditText>(R.id.editTextWeight)
            if (checkGrade(category, weight, rawGrades)) {
                rawGrades.put(category.text.toString(), weight.text.toString().toDouble())
            } else {
                rawGrades.clear()
                break
            }
        }

        if (rawGrades.isNotEmpty()) { // guaranteed to be non-empty if successful
            try { // will error if the total is not ~100
                val g = Grades(rawGrades, getString(R.string.weightingError))
            } catch (e: RuntimeException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Ensure the text fields are not empty or duplicates
    // return true if all of them are not, else error and return false
    private fun checkGrade(category: EditText, weight: EditText, rawGrades: Map<String, Double>): Boolean {
        if (category.text.toString().isBlank()) {
            category.error = getString(R.string.field_blank)
            return false
        } else if (category.text.toString() in rawGrades) {
            category.error = getString(R.string.field_exists)
            return false
        } else if (weight.text.toString().isBlank()) {
            weight.error = getString(R.string.field_blank)
            return false
        }
        return true
    }

    // List item for each category and weight pair
    private class CategoryAdapter(context: Context, cats: MutableList<Boolean>): ArrayAdapter<Boolean>(context, 0, cats) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            if (convertView == null) {
                return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            }
            return convertView
        }
    }
}