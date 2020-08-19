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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_scores.*

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        val g = intent.getSerializableExtra(GRADES) as Grades?
        val categories = g?.categories
        setNextCategory(categories)

        scores_list.apply {
            layoutManager = LinearLayoutManager(this@ScoresActivity)
            adapter = ScoreAdapter()
        }
    }

    // Set layout element currentCategory
    private fun setNextCategory(categories: ListIterator<String>?): Boolean {
        val hasNext = categories?.hasNext() == true
        if (hasNext) currentCategory.text = categories!!.next()
        return hasNext
    }

    // Add Another button: adds another field to input text
    fun addAnother(view: View) {
        ScoreContent.addItem()
        scores_list.apply {
            adapter?.notifyItemInserted(ScoreContent.size - 1)
            adapter?.notifyItemChanged(ScoreContent.size - 2) // Change keyboard button from Done to Next
            scrollToPosition(ScoreContent.size) // button
        }
    }

    // Adapter for scores_list
    private class ScoreAdapter: RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ScoreAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreAdapter.ViewHolder, position: Int) {
            if (getItemViewType(position) == R.layout.score_item) {
                val item = ScoreContent.ITEMS[position]
                holder.mScoreLabel?.apply {
                    setText(item.score)
                    error = item.scoreError
                    imeOptions =
                        if (position == itemCount - 2) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
                }
            }
        }

        override fun getItemCount() = ScoreContent.size + 1 // add 1 to account for button

        override fun getItemViewType(position: Int) =
            if (position == itemCount - 1) R.layout.add_another_button else R.layout.score_item

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val mScoreLabel: EditText? = view.findViewById(R.id.editTextScore)

            init { // set category and weight when text changed
                mScoreLabel?.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                        val item = ScoreContent.ITEMS[adapterPosition]
                        item.score = mScoreLabel.text.toString()
                        // only reset error when actual changes occur to text (error remains when device orientation changes)
                        if (count > 0) item.scoreError = null
                    }
                })
            }
        }
    }

    // list that holds each row's category, weight, and corresponding errors
    object ScoreContent {
        val ITEMS = mutableListOf<ScoreItem>()
        val size get() = ITEMS.size

        init {
            addItem()
        }

        fun addItem() {
            ITEMS.add(ScoreItem())
        }

        data class ScoreItem(
            var score: String = "",
            var scoreError: String? = null
        )
    }
}