package com.ds.simplegradecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
    // TODO: Set focus on last EditText
    fun addCategory(view: View) {
        CategoryContent.addItem()
        categories_list.adapter?.notifyItemInserted(CategoryContent.size - 1)
    }

    // Continue button: Verifies scores, creates Grades class, and continues onto the next page
    // TODO: Pass Grades g into next activity
    fun createGrades(view: View) {
        val rawGrades = mutableMapOf<String, Double>()
        val categories = mutableSetOf<String>() // used for error checking when field already exists
        // may contain more categories than rawGrades since pair will not be added to rawGrades if weight is empty
        var success = true
        for (i in 0 until CategoryContent.size) {
            val current = categories_list.getChildAt(i)
            val category = current.findViewById<EditText>(R.id.editTextCategory)
            val weight = current.findViewById<EditText>(R.id.editTextWeight)
            if (checkGrade(category, weight, categories, i) && success) { // find all errors, even if success is false
                rawGrades[category.text.toString()] = weight.text.toString().toDouble()
            } else if (success) {
                rawGrades.clear()
                success = false
            }
            if (category.text.toString().isNotBlank()) categories.add(category.text.toString())
        }

        if (success) {
            try { // will error if the total is not ~100
                val g = Grades(rawGrades, getString(R.string.weightingError))
            } catch (e: RuntimeException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Ensure the text fields are not empty or duplicates
    // return true if all of them are not, else error and return false
    private fun checkGrade(category: EditText, weight: EditText, categories: Set<String>, i: Int): Boolean {
        var c = true
        var w = true
        if (category.text.toString().isBlank()) {
            category.error = getString(R.string.field_blank)
            c = false
        }
        if (category.text.toString() in categories) {
            category.error = getString(R.string.field_exists)
            c = false
        }
        if (weight.text.toString().isBlank()) {
            weight.error = getString(R.string.field_blank)
            w = false
        }
        if (!c) CategoryContent.ITEMS[i].categoryError = category.error?.toString()
        if (!w) CategoryContent.ITEMS[i].weightError = weight.error?.toString()
        return c && w
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
                holder.mCategoryLabel.error = item.categoryError
                holder.mCategoryLabel.imeOptions = EditorInfo.IME_ACTION_NEXT

                holder.mWeightLabel!!.setText(item.weight)
                holder.mWeightLabel.error = item.weightError
                holder.mWeightLabel.imeOptions =
                    if (position == itemCount - 2) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
            }
        }

        override fun getItemCount() = CategoryContent.size + 1 // add 1 to account for button

        override fun getItemViewType(position: Int) =
            if (position == itemCount - 1) R.layout.add_another_button else R.layout.list_item

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val mCategoryLabel: EditText? = view.findViewById<EditText>(R.id.editTextCategory)
            val mWeightLabel: EditText? = view.findViewById<EditText>(R.id.editTextWeight)

            init { // set category and weight when text changed
                mCategoryLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                        val item = CategoryContent.ITEMS[adapterPosition]
                        item.category = mCategoryLabel.text.toString()
                        // only change error when actual changes occur to text (error remains when device orientation changes)
                        if (count > 0) item.categoryError = mCategoryLabel.error?.toString()
                    }
                })

                mWeightLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                        val item = CategoryContent.ITEMS[adapterPosition]
                        item.weight = mWeightLabel.text.toString()
                        // only change error when actual changes occur to text (error remains when device orientation changes)
                        if (count > 0) item.weightError = mWeightLabel.error?.toString()
                    }
                })
            }
        }
    }

    // list that holds each row's category, weight, and corresponding errors
    object CategoryContent {
        val ITEMS = mutableListOf<CategoryItem>()
        val size get() = ITEMS.size

        init {
            addItem()
        }

        fun addItem() {
            ITEMS.add(CategoryItem("", "", null, null))
        }

        data class CategoryItem(var category: String, var weight: String, var categoryError: String?, var weightError: String?)
    }
}