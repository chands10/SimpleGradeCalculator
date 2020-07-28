package com.ds.simplegradecalculator

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class GradesTest {
    private val e = Grades(mapOf())
    private val f = Grades(mapOf("tests" to 1.0))
    private val g = Grades(mapOf("tests" to 0.6, "projects" to 0.2, "quizzes" to 0.2))

    @Before
    fun setUp() {
        g.addScores("tests", listOf(100.0))
    }

    @Test
    fun testConstructor() {
        //empty grades
        Grades(mapOf())
        Grades(mutableMapOf())

        // sum of weightings = 1
        Grades(mapOf("tests" to 1.0))
        Grades(mapOf("tests" to 0.4, "quizzes" to 0.2, "hws" to 0.2, "labs" to 0.2))
    }

    @Test
    fun testAddScores() {
        assertNull(e.addScores("tests", listOf(99.0))) // empty grades
        assertNull(f.addScores("quizzes", listOf(99.0))) // quizzes is not a category
        assertEquals(false, f.addScores("tests", listOf())) // list does not change
        assertEquals(true, f.addScores("tests", listOf(99.1, 100.0)))
        assertEquals(true, g.addScores("tests", listOf(0.0)))
    }

    @Test
    fun testCalculateGrade() {
        assertEquals(100.0, e.calculateGrade(), 0.0) // no grades -> 100
        assertEquals(100.0, f.calculateGrade(), 0.0) // category but no grades -> 100
        assertEquals(100.0, g.calculateGrade(), 0.0) // 100 in one category -> 100

        g.addScores("tests", listOf(0.0))
        assertEquals(50.0, g.calculateGrade(), 0.0) // 50 avg in one category -> 50
        g.addScores("projects", listOf(100.0, 100.0))
        assertEquals(62.5, g.calculateGrade(), 0.0) // test avg: 50, scale: .6/.8; project avg: 100, scale: .2/.8
        g.addScores("quizzes", listOf(75.0, 25.0))
        assertEquals(60.0, g.calculateGrade(), 0.0) // test avg: 50, project avg: 100, quiz avg: 50
    }
}