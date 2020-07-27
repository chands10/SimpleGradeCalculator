package com.ds.simplegradecalculator
// [rawGrades] with category as key and weight as value
class Grades(val rawGrades: Map<String, Double>) {
    // category named [name] and with percentage of grade [weighting]
    class Category(val name: String, val weighting: Double) {
        val scores = mutableListOf<Double>()
    }

    val grades = rawGrades.mapValues { Category(it.key, it.value) }

    fun addScores(category: String, scores: List<Double>) {
        TODO()
    }

    fun calculateGrade() {
        TODO()
    }
}