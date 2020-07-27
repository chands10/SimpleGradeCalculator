package com.ds.simplegradecalculator
// [rawGrades] with category as key and weight as value
class Grades(rawGrades: Map<String, Double>) {
    // category named [name] and with percentage of grade [weighting]
    class Category(val name: String, val weighting: Double) {
        val scores = mutableListOf<Double>()
    }

    private val grades = rawGrades.mapValues { Category(it.key, it.value) }

    // add a list of [scores] to [category] and return true if successful
    fun addScores(category: String, scores: List<Double>) = grades[category]?.scores?.addAll(scores)

    // calculate the total grade and return it
    fun calculateGrade(): Double {
        var total = 0.0
        for (g in grades) total += g.value.scores.average() * g.value.weighting
        return total
    }
}