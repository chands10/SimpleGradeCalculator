package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        categories_list.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = CategoryAdapter()
        }
    }

    // Add Another button: adds another field to input text
    fun addCategory(view: View) {
        CategoryContent.addItem()
        categories_list.adapter?.notifyDataSetChanged()
    }

    // Continue button: Verifies scores, creates Grades class, and continues onto the next page
    // TODO: Pass Grades g into next activity
    fun createGrades(view: View) {
        val rawGrades = mutableMapOf<String, Double>()
        for (i in 0 until CategoryContent.size()) {
            val current = categories_list.getChildAt(i)
            val category = current.findViewById<EditText>(R.id.editTextCategory)
            val weight = current.findViewById<EditText>(R.id.editTextWeight)
            if (checkGrade(category, weight, rawGrades)) {
                rawGrades[category.text.toString()] = weight.text.toString().toDouble()
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

    // Adapter for categories_list
    private class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CategoryAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
            if (getItemViewType(position) == R.layout.list_item) {
                val item = CategoryContent.ITEMS[position]
                holder.mCategoryLabel!!.setText(item.category)
                holder.mWeightLabel!!.setText(item.weight)
            }
        }

        override fun getItemCount() = CategoryContent.size() + 1 // add 1 to account for button

        override fun getItemViewType(position: Int) =
            if (position == itemCount - 1) R.layout.add_another_button else R.layout.list_item

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val mCategoryLabel: EditText? = view.findViewById<EditText>(R.id.editTextCategory)
            val mWeightLabel: EditText? = view.findViewById<EditText>(R.id.editTextWeight)

            init { // set category and weight when text changed
                mCategoryLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        CategoryContent.ITEMS[adapterPosition].category = mCategoryLabel.text.toString()
                    }
                })

                mWeightLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        CategoryContent.ITEMS[adapterPosition].weight = mWeightLabel.text.toString()
                    }
                })
            }
        }
    }

    // list that holds each row's category and weight
    object CategoryContent {
        val ITEMS = mutableListOf<CategoryItem>()
        init {
            addItem()
        }

        fun addItem() {
            ITEMS.add(CategoryItem("", ""))
        }

        fun size() = ITEMS.size

        data class CategoryItem(var category: String, var weight: String)
    }
}