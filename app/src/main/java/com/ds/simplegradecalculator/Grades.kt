package com.ds.simplegradecalculator
// [rawGrades] with category as key and weight as value
class Grades(private val rawGrades: Map<String, Double>) {
    // category with percentage of grade [weighting] and test grades [scores]
    class Category(val weighting: Double) {
        val scores = mutableListOf<Double>()
    }
    private val c = checkRep()
    private val grades = rawGrades.mapValues { Category(it.value) }

    @Throws(RuntimeException::class)
    private fun checkRep() {
        if (rawGrades.isNotEmpty() &&  rawGrades.values.sum() != 1.0) {
            throw RuntimeException("Weighting must be equal to 1")
        }
    }

    // add a list of [scores] to [category] and return true if successful else return false/null
    fun addScores(category: String, scores: List<Double>) = grades[category]?.scores?.addAll(scores)

    // calculate the total grade and return it
    fun calculateGrade(): Double {
        //update weightings to account for empty categories
        val trueGrades = grades.filter { it.value.scores.isNotEmpty() }
        val w = trueGrades.map { it.value.weighting }.sum()

        if (w == 0.0) return 100.0 // grades are empty or all categories of grades are empty
        return trueGrades.map { it.value.scores.average() * it.value.weighting / w }.sum()
    }
}