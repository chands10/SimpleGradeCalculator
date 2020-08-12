package com.ds.simplegradecalculator
// [rawGrades] with category as key and weight as value and string [weightingError]
class Grades(private val rawGrades: Map<String, Double>,
             private val weightingError: String = "Total weighting must be equal to 100") {
    // category with percentage of grade [weighting] and test grades [scores]
    private inner class Category(val weighting: Double) {
        val scores = mutableListOf<Double>()
    }
    private val c = checkRep()
    private val grades = rawGrades.mapValues { Category(it.value) }

    @Throws(RuntimeException::class)
    private fun checkRep() {
        val sum = rawGrades.values.sum()
        if (rawGrades.isNotEmpty() && (sum < 99.89 || sum > 100.101)) {
            throw RuntimeException(weightingError)
        }
    }

    // add a list of [scores] to [category]
    // return true if successful else return false if list does not change and null otherwise
    // TODO: Might delete function and convert scores to immutable list if previous button is implemented on page 2
    fun addScores(category: String, scores: List<Double>) = grades[category]?.scores?.addAll(scores)

    // return an immutable list of scores of a [category] to be viewed
    fun getScores(category: String) = grades[category]?.scores?.toList()

    // copy [scores] and set them to the grades class with key [category]
    // return null if category does not exist, false if list is empty, and true otherwise
    // (return may be removed in future if addScores is deleted)
    fun setScores(category: String, scores: List<Double>): Boolean? {
        grades[category]?.scores?.clear()
        return addScores(category, scores)
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