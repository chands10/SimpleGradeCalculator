package com.ds.simplegradecalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_scores.*

class ScoresActivity : AppCompatActivity() {
    private var categories: List<String>? = null // categories in grades
    private var c = -1 // current category index
    private var g: Grades? = null
    private var callback: OnBackPressedCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        g = intent.getSerializableExtra(GRADES) as Grades?
        categories = g?.categories
        setAdjacentCategory()

        scores_list.apply {
            layoutManager = LinearLayoutManager(this@ScoresActivity)
            adapter = ScoreAdapter()
        }

        callback = object: OnBackPressedCallback(false) {
            override fun handleOnBackPressed() = makeChange(true)
        }
        onBackPressedDispatcher.addCallback(this, callback as OnBackPressedCallback)
    }

    // Set layout element currentCategory
    private fun setAdjacentCategory(previous: Boolean = false): Boolean {
        val hasAdjacent = if (previous) c > 0 && c < categories?.size ?: 0 else c < categories?.lastIndex ?: -1
        if (hasAdjacent) {
            c = if (previous) c - 1 else c + 1
            currentCategory.text = categories!![c]
            if (c == categories?.lastIndex) nextButton.text = getString(R.string.calculate)
            else nextButton.text = getString(R.string.next)
            callback?.isEnabled = c != 0
        }
        return hasAdjacent
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

    // TODO: Evaluate if error checking is needed
    // save the scores of the current category inside Grades g
    private fun saveScores(category: String?) = g?.setScores(category, ScoreContent.ITEMS
        .filter { it.score.toDoubleOrNull() != null }
        .map { it.score.toDouble() })

    // Save current data in grades, set current to the adjacent category if available,
    // and repopulate ScoreContent
    private fun makeChange(previous: Boolean = false) {
        // save previous scores
        saveScores(categories?.get(c))

        // update current if relevant
        val change = setAdjacentCategory(previous)
        when {
            change -> { // repopulate data
                val scores = g?.getScores(categories?.get(c))

                // clear data
                val s = ScoreContent.size
                ScoreContent.ITEMS.clear()
                scores_list.adapter?.notifyItemRangeRemoved(0, s)

                if (scores?.isNotEmpty() == true) {
                    scores.mapTo(ScoreContent.ITEMS) { ScoreContent.ScoreItem(it.toString()) }
                    scores_list.adapter?.notifyItemRangeInserted(0, ScoreContent.size)
                }
                else {
                    ScoreContent.addItem()
                    scores_list.adapter?.notifyItemInserted(0)
                }
            }
            previous -> { // go back to previous activity
                // TODO: Consider passing Grades g back
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
            }
            else -> { // prepare for calculation
                // TODO: else switch activities
                Toast.makeText(applicationContext, g?.calculateGrade().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // call makeChange(). If at beginning of categories then go to previous activity
    fun prev(view: View) = onBackPressed()

    // Call makeChange(). If at end of categories then switch activities
    fun next(view: View) = makeChange()

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
//                    error = item.scoreError
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
//                        if (count > 0) item.scoreError = null
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

        fun addItem() { // notify change after
            ITEMS.add(ScoreItem())
        }

        data class ScoreItem(
            var score: String = ""
//            var scoreError: String? = null
        )
    }
}