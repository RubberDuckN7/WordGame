package com.distantlandgames.tetrisandwords.tetris

import android.util.Log
import java.nio.file.Files.size

class LexiconHandler {
    var allWords: ArrayList<List<String>>
    var startPosPermute: ArrayList<Int>
    var endPosPermute: ArrayList<Int>

    init {
        allWords = ArrayList<List<String>>()

        startPosPermute = ArrayList<Int>()
        endPosPermute = ArrayList<Int>()

        var size = 10

        for (i in 0..size) {
            var start = size - i
            for (j in start downTo 0) {
                startPosPermute.add(i)
                endPosPermute.add(j)
            }
        }
    }

    fun findWords(stringWithWords: String): ArrayList<String>  {
        var results = ArrayList<String>()

        for(list in allWords) {
            var localResults = findWords(startPosPermute, endPosPermute, stringWithWords, list)
            for(word in localResults) {
                results.add(word)
            }
        }

        return results
    }

    fun addWordList(wordList: List<String>) {
        allWords.add(wordList)
    }

    private fun findWords(startPosPermute: ArrayList<Int>, endPosPermute: ArrayList<Int>, wordWithNoise: String, wordList: List<String>): ArrayList<String> {
        var permutations = getPermutations(startPosPermute.size, startPosPermute, endPosPermute, wordWithNoise)
        var localFoundWordsList = ArrayList<String>()

        var debugStr = ""
        for(permute in permutations) {
            debugStr += "Permute: " + permute + "\n"
            if(permute != "" && wordList.contains(permute)) {
                localFoundWordsList.add(permute)
            }
        }

        //Log.d("VIOLET", debugStr)
        return localFoundWordsList
    }

    fun getPermutations(size: Int, startPos: ArrayList<Int>, endPos: ArrayList<Int>, wordWithNoise: String): ArrayList<String> {
        val permutations = ArrayList<String>()
        for (i in 0 until size) {
            permutations.add(trimForHash(startPos[i], endPos[i], 10, wordWithNoise))
        }

        return permutations
    }

    fun trimForHash(start: Int, end: Int, length: Int, wordWithNoise: String): String {
        var value = ""
        try {
            value = wordWithNoise.substring(start, (length) - (end))
            //Log.d("VIOLET", "Trimmed: " + value + " setTimeLimit: " + setTimeLimit + " end: " + end)
        } catch (e: Exception) {
            System.out.println("Trim for hash failed, word size: " + wordWithNoise.length + " setTimeLimit: " + start + " end: " + end)
            Log.d("VIOLET", "Exception happened: " + "setTimeLimit: " + start + " end: " + end)
        }
        return value
    }

    private fun clearLists() {
        for(list in allWords) {

        }
        allWords.clear()
    }
}