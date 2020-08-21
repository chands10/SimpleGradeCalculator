package com.ds.simplegradecalculator

import android.content.Intent
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

const val GRADES = "com.ds.simplegradecalculator.GRADES"

class MainActivity : AppCompatActivity() {
    private var oldGrades: Grades? = null // return oldGrades if going back from ScoresActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        categories_list.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = CategoryAdapter(getString(R.string.field_exists))
        }
    }

    // Add Another button: adds another field to input text
    fun addAnother(view: View) {
        CategoryContent.addItem()
        categories_list.apply {
            adapter?.notifyItemInserted(CategoryContent.size - 1)
            adapter?.notifyItemChanged(CategoryContent.size - 2) // Change keyboard button from Done to Next
            scrollToPosition(CategoryContent.size) // button
        }
    }

    // Continue button: Verifies scores, creates Grades class, and continues onto the next page
    fun createGrades(view: View) {
        val rawGrades = mutableMapOf<String, Double>()
        val categories = mutableSetOf<String>() // used for error checking when field already exists
        // may contain more categories than rawGrades since pair will not be added to rawGrades if weight is empty
        var success = true
        for (i in 0 until CategoryContent.size) {
            val current = CategoryContent.ITEMS[i]
            val category = current.category
            val weight = current.weight
            if (checkGrade(category, weight, categories, i) && success) { // find all errors, even if success is false
                rawGrades[category] = weight.toDouble()
            } else if (success) {
                rawGrades.clear()
                success = false
            }
            if (category.isNotBlank()) categories.add(category)
        }

        if (success) {
            var g: Grades? = null
            try { // will error if the total is not ~100
                g = Grades(rawGrades)
            } catch (e: RuntimeException) {
                Toast.makeText(applicationContext, getString(R.string.weightingError), Toast.LENGTH_SHORT).show()
            }
            if (g != null) {
                // update g with data found in oldGrades
                oldGrades?.categories?.forEach { g.setScores(it, oldGrades?.getScores(it)) }

                val intent = Intent(this, ScoresActivity::class.java).apply {
                    putExtra(GRADES, g)
                }
                startActivityForResult(intent, 1)
            }
        }
    }

    // set oldGrades
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) oldGrades = data?.getSerializableExtra(GRADES) as Grades?
    }

    // Ensure the text fields are not empty or duplicates
    // return true if all of them are not, else error and return false
    private fun checkGrade(category: String, weight: String, categories: Set<String>, i: Int): Boolean {
        var r = true
        var existsText = false
        when {
            category.isBlank() -> {
                CategoryContent.ITEMS[i].categoryError = getString(R.string.field_blank)
                r = false
            }
            category in categories -> {
                CategoryContent.ITEMS[i].categoryError = getString(R.string.field_exists)
                r = false
            }
            CategoryContent.ITEMS[i].categoryError == getString(R.string.field_exists) -> { // remove any exist errors if they were present prior
                // ex: two same categories, user changes top category. Now set bottom categoryError to null
                CategoryContent.ITEMS[i].categoryError = null
                existsText = true
            }
        }
        if (weight.isBlank()) {
            CategoryContent.ITEMS[i].weightError = getString(R.string.field_blank)
            r = false
        }
        if (!r || existsText) categories_list.adapter?.notifyItemChanged(i)
        return r
    }

    // Adapter for categories_list
    private class CategoryAdapter(val fieldExistsError: String): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
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
                holder.mCategoryLabel?.apply {
                    setText(item.category)
                    error = item.categoryError
                    imeOptions = EditorInfo.IME_ACTION_NEXT
                }

                holder.mWeightLabel?.apply {
                    setText(item.weight)
                    error = item.weightError
                    imeOptions =
                        if (position == itemCount - 2) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
                }
            }
        }

        override fun getItemCount() = CategoryContent.size + 1 // add 1 to account for button

        override fun getItemViewType(position: Int) =
            if (position == itemCount - 1) R.layout.add_another_button else R.layout.list_item

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val mCategoryLabel: EditText? = view.findViewById(R.id.editTextCategory)
            val mWeightLabel: EditText? = view.findViewById(R.id.editTextWeight)

            init { // set category and weight when text changed
                mCategoryLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                        val item = CategoryContent.ITEMS[adapterPosition]
                        // only reset error when actual changes occur to text (error remains when device orientation changes)
                        val input = mCategoryLabel.text.toString()
                        if (item.categoryError == fieldExistsError && input != item.category) item.categoryError = null
                        if (count > 0 && item.categoryError != fieldExistsError) item.categoryError = null

                        item.category = input
                    }
                })

                mWeightLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                        val item = CategoryContent.ITEMS[adapterPosition]
                        // only reset error when actual changes occur to text (error remains when device orientation changes)
                        if (count > 0) item.weightError = null

                        item.weight = mWeightLabel.text.toString()
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
            ITEMS.add(CategoryItem())
        }

        data class CategoryItem(
            var category: String = "",
            var weight: String = "",
            var categoryError: String? = null,
            var weightError: String? = null
        )
    }
}