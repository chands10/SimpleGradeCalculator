package com.ds.simplegradecalculator

import java.io.Serializable

// [rawGrades] with category as key and weight as value and string [weightingError]
class Grades(rawGrades: Map<String, Double>): Serializable {
    // category with percentage of grade [weighting] and test grades [scores]
    private class Category(val weighting: Double): Serializable {
        var scores = listOf<Double>()
    }
    init {
        checkRep(rawGrades)
    }

    private val grades = rawGrades.mapValues { Category(it.value) }

    // LinkedHashMap preserves order of items inserted
    val categories = grades.keys.toList()

    @Throws(RuntimeException::class)
    private fun checkRep(rawGrades: Map<String, Double>) {
        val sum = rawGrades.values.sum()
        if (rawGrades.isNotEmpty() && (sum < 99.89 || sum > 100.101)) {
            throw RuntimeException()
        }
    }

    // add a list of [scores] to [category]
    // return true if successful else return false if list does not change and null otherwise
//    fun addScores(category: String?, scores: List<Double>) = grades[category]?.scores?.addAll(scores)

    // return an immutable list of scores of a [category] to be viewed
    fun getScores(category: String?) = grades[category]?.scores

    // set [scores] to the grades class with key [category]
    fun setScores(category: String?, scores: List<Double>?) {
        if (scores != null) grades[category]?.scores = scores
    }

    // calculate the total grade and return it
    fun calculateGrade(): Double {
        //update weightings to account for empty categories
        val trueGrades = grades.filter { it.value.scores.isNotEmpty() }
        val w = trueGrades.map { it.value.weighting }.sum()

        if (w == 0.0) return 100.0 // grades are empty or all categories of grades are empty
        return trueGrades.map { it.value.scores.average() * it.value.weighting }.sum() / w
    }
}