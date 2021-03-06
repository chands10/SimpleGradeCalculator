package com.ds.simplegradecalculator

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class GradesTest {
    private val e = Grades(mapOf())
    private val f = Grades(mapOf("tests" to 100.0))
    private val g = Grades(mapOf("tests" to 60.0, "projects" to 20.0, "quizzes" to 20.0))

    @Before
    fun setUp() {
        g.setScores("tests", listOf(100.0))
    }

    @Test
    fun testConstructor() {
        //empty grades
        Grades(mapOf())
        Grades(mutableMapOf())

        // sum of weightings = ~100
        Grades(mapOf("tests" to 100.0))
        Grades(mapOf("tests" to 40.0, "quizzes" to 20.0, "hws" to 20.0, "labs" to 20.0))
        Grades(mapOf("tests" to 33.3, "quizzes" to 33.3, "hws" to 33.3))
    }

    @Test
    fun testCategories() {
        val ec = e.categories
        assertTrue(ec.isEmpty())

        val fc = f.categories
        assertEquals(listOf("tests"), fc)

        val gc = g.categories
        assertEquals(listOf("tests", "projects", "quizzes"), gc) // order is preserved
    }

    @Test
    fun testGetScores() {
        assertTrue(g.getScores("tests") is List<Double>) // immutable

        assertNull(e.getScores("tests")) // empty grades
        assertNull(g.getScores("labs")) // labs is not a category
        assertNull(g.getScores(null))
        assertEquals(listOf<Double>(), g.getScores("quizzes")) // quizzes is empty
        assertEquals(listOf(100.0), g.getScores("tests"))
    }

    @Test
    fun testSetScores() {
        e.setScores("tests", listOf(100.0)) // empty grades
        assertNull(e.getScores("tests"))

        g.setScores("labs", listOf(100.0)) // labs is not a category
        assertNull(g.getScores("labs"))

        g.setScores(null, listOf(100.0))
        assertNull(g.getScores("labs"))

        g.setScores("quizzes", listOf(100.0)) // start with empty list
        assertEquals(listOf<Double>(100.0), g.getScores("quizzes"))

        g.setScores("tests", listOf(0.0, 50.0)) // initially [100.0]
        assertEquals(listOf(0.0, 50.0), g.getScores("tests")) // 100.0 is removed

        g.setScores("tests", null)
        assertEquals(listOf(0.0, 50.0), g.getScores("tests")) // nothing changes
    }

    @Test
    fun testCalculateGrade() {
        assertEquals(100.0, e.calculateGrade(), 0.0) // no grades -> 100
        assertEquals(100.0, f.calculateGrade(), 0.0) // category but no grades -> 100
        assertEquals(100.0, g.calculateGrade(), 0.0) // 100 in one category -> 100

        g.setScores("tests", listOf(100.0, 0.0))
        assertEquals(50.0, g.calculateGrade(), 0.0) // 50 avg in one category -> 50
        g.setScores("projects", listOf(100.0, 100.0))
        assertEquals(62.5, g.calculateGrade(), 0.0) // test avg: 50, scale: .6/.8; project avg: 100, scale: .2/.8
        g.setScores("quizzes", listOf(75.0, 25.0))
        assertEquals(60.0, g.calculateGrade(), 0.0) // test avg: 50, project avg: 100, quiz avg: 50
    }
}