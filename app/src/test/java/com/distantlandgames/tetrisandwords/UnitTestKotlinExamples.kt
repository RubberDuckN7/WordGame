package com.distantlandgames.tetrisandwords

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 Let's try some cool stuff, like mockito!
 Along with kotlin features, obviously...
 */

enum class Days {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
}

enum class RGB (
    val R: Int, val G: Int, val B: Int
) {
    // An example where semicolon is actually required!
    ColorOne(100, 200, 255),
    ColorRed(255, 0, 0);

    fun getSum(): Int { return R + G + B }
}

class UnitTestKotlinExamples {

    fun findRed(color: RGB) {
        when(color) {
            RGB.ColorRed -> "True"
            RGB.ColorOne -> "False"
        }
    }

    fun findRedCombine(color: RGB) = when(color) {
        RGB.ColorRed -> "True"
        RGB.ColorOne -> "False"
    }

    fun findRedNoParam(color: RGB) = when {
        color == RGB.ColorOne -> "False"
        color == RGB.ColorRed -> "True"
        else -> "Default"
    }

    @Test
    fun enumClass_Test() {
        // Just for visualization.
        val middleDay = Days.Wednesday
        assertEquals(middleDay, Days.Wednesday)

        assertNotEquals(RGB.ColorRed.getSum(), RGB.ColorOne.getSum())
    }

    @Test
    fun loop_Test() {
        val mapOfLetters = TreeMap<Char, String>()
        for(c in 'A' .. 'F') {
            val binary =  Integer.toBinaryString(c.toInt())
            mapOfLetters[c] = binary
        }

        for((letter, binary) in mapOfLetters) {
            println("$letter = $binary")
        }
    }
}
