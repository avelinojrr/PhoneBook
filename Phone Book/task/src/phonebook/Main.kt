package phonebook

import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.*

var totalList = File("./src/resources/directory.txt").readText()
var searchingNames = File("./src/resources/find.txt").readText()
var linearSearchTime: Long = 0L
var bubbleSortTime: Long = 0L
var jumpSearchTime: Long = 0L
var quickSortTime: Long = 0L
var binarySearchTime: Long = 0L

fun main() {
    linearSearch(totalList, searchingNames)
    bubbleSort(totalList.lines().toMutableList())
    quickSort(totalList.lines().toMutableList())
    hashTable(totalList, searchingNames)
}

fun linearSearch(totalList: CharSequence, searchTerm: CharSequence) {
    println("Start searching (linear search)...")
    val startTime = System.currentTimeMillis()
    val totalCount = searchTerm.lines().count()
    var count = 0
    for (name in searchTerm.lines()) {
        if (totalList.contains(name)) {
            count++
            continue
        }
    }
    linearSearchTime = System.currentTimeMillis() - startTime
    println("Found $count / $totalCount entries. Time taken: ${linearSearchTime.toFormattedTime()}")
}

fun bubbleSort(totalList: MutableList<String>): String {
    println("Start searching (bubble sort + jump search)...")
    var swap = true
    val startTime = System.currentTimeMillis()
    loop@ while (swap) {
        swap = false
        for (index in 0 until totalList.size - 1) {
            if (totalList[index].drop(8) > totalList[index + 1].drop(8)) {
                val temp = totalList[index]
                totalList[index] = totalList[index + 1]
                totalList[index + 1] = temp
                val midTime = System.currentTimeMillis() - startTime
                if (midTime > linearSearchTime.times(10))
                    break@loop
                swap = true
            }
        }
    }
    bubbleSortTime = System.currentTimeMillis() - startTime
    jumpSearch(totalList, searchingNames)
    return totalList.toString()
}

fun jumpSearch(totalList: List<String>, searchTerm: CharSequence) {
    val blockSize = floor(sqrt(totalList.size.toDouble())).toInt()
    var count = 0
    val startTime = System.currentTimeMillis()
    for (name in searchTerm.lines()) {
        var currentLastIndex = blockSize - 1
        for (index in 0 until totalList.size - 1) {
            if (totalList[index].contains(name)) {
                while (currentLastIndex < totalList.size && index > currentLastIndex) {
                    currentLastIndex += blockSize
                }
            }
        }
        var currentSearchIndex = currentLastIndex - blockSize + 1
        while (currentSearchIndex <= currentLastIndex && currentSearchIndex < totalList.size) {
            if (totalList[currentSearchIndex].contains(name))
                count++
            currentSearchIndex++
        }
    }
    jumpSearchTime = System.currentTimeMillis() - startTime
    println("Found $count / ${searchTerm.lines().count()} entries. " +
            "Time taken: ${(bubbleSortTime + jumpSearchTime).toFormattedTime()}")
    println("Sorting time: ${bubbleSortTime.toFormattedTime()} - STOPPED, moved to linear search")
    println("Searching time: ${jumpSearchTime.toFormattedTime()}")
}

fun quickSort(totalList: List<String>) {
    println("Start searching (quick sort + binary search)...")
    sorting(totalList)
    binarySearch(totalList, searchingNames)
}

fun sorting(totalList: List<String>): List<String> {
    val startTime = System.currentTimeMillis()
    if (totalList.count() < 2) return totalList

    val pivot = totalList.elementAt(totalList.lastIndex).substringAfter(' ')
    val equal = totalList.filter { it.substringAfter(' ') == pivot }
    val less = totalList.filter { it.substringAfter(' ') < pivot }
    val greater = totalList.filter { it.substringAfter(' ') > pivot }

    val finalList = sorting(less) + equal + sorting(greater)
    quickSortTime = System.currentTimeMillis() - startTime

    return finalList
}

fun binarySearch(totalList: List<String>, searchTerm: CharSequence): Int {
    val startTime = System.currentTimeMillis()
    var startIndex = 0
    var endIndex = totalList.size - 1
    var midIndex: Int
    var count = 0
    while (startIndex <= endIndex) {
        for (name in searchTerm.lines()) {
            midIndex = (startIndex + endIndex) / 2
            when {
                name == totalList[midIndex].substringAfter(' ') -> return midIndex
                name > totalList[midIndex].substringAfter(' ') -> startIndex = midIndex + 1
                name < totalList[midIndex].substringAfter(' ') -> endIndex = midIndex - 1
            }
            count++
        }
    }
    binarySearchTime = System.currentTimeMillis() - startTime
    println("Found $count / ${searchTerm.lines().count()} entries. " +
            "Time taken: ${(quickSortTime + binarySearchTime).toFormattedTime()}")
    println("QuickSort time: ${quickSortTime.toFormattedTime()}")
    println("Binary Search time: ${binarySearchTime.toFormattedTime()}")
    return -1
}

fun hashTable(totalList: CharSequence, searchTerm: CharSequence) {
    println("Start searching (hash table)...")
    val startCreateTime = System.currentTimeMillis()
    val hashMap = HashTable<String, String>()
    for (name in totalList.lines()) {
        hashMap.insert(name.substringAfter(' '), name.substringBefore(' '))
    }
    val hashTableCreateTime = System.currentTimeMillis() - startCreateTime

    // Start search
    val startSearchTime = System.currentTimeMillis()
    var count = 0
    for (name in searchTerm.lines()) {
        hashMap.get(name)
        count++
    }
    val hashTableSearchTime = System.currentTimeMillis() - startSearchTime
    println("Found $count / ${searchTerm.lines().count()} entries. " +
            "Time taken: ${(hashTableCreateTime + hashTableSearchTime).toFormattedTime()}")
    println("Creating time: ${hashTableCreateTime.toFormattedTime()}")
    println("Searching time: ${hashTableSearchTime.toFormattedTime()}")
}

fun Long.toFormattedTime(): String = String.format(
    "%01d min. %01d sec. %01d ms.",
    TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1),
    TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1),
    TimeUnit.MILLISECONDS.toMillis(this % TimeUnit.SECONDS.toMillis(1))
)
