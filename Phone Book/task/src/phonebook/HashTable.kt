package phonebook

import java.util.*
import kotlin.math.abs

class HashTable<K, V> {
    private data class Entry<K, V>(val key: K, val value: V)
    private val loadFactor = 0.75
    private var arraySize = 16
    private var numberOfEntries = 0
    private var entries: Array<LinkedList<Entry<K, V>>> = Array(arraySize) { LinkedList<Entry<K, V>>() }

    fun insert(key: K, value: V) {
        numberOfEntries++
        if (numberOfEntries > arraySize * loadFactor) {
            increaseCapacity()
        }
        put(key, value, entries)
    }

    fun get(key: K): V? {
        val index = calculateHashCode(key)
        val listAtArraySlot = entries[index]

        return listAtArraySlot.find { it.key == key }?.value
    }

    private fun put(key: K, value: V, localEntries: Array<LinkedList<Entry<K, V>>>) {
        val index = calculateHashCode(key)
        val listAtArraySlot = localEntries[index]
        val newEntry = Entry(key, value)

        // Check if the key already exists in the LinkedList entries
        val indexOfEntryInList = listAtArraySlot.indexOfFirst { it.key == key }
        if (indexOfEntryInList >= 0) {
            listAtArraySlot[indexOfEntryInList] = newEntry
        } else {
            listAtArraySlot.offer(newEntry)
        }
    }

    private fun increaseCapacity() {
        arraySize *= 2

        // Create a new array and add all the exiting items to the bigger table
        val localEntries: Array<LinkedList<Entry<K, V>>> = Array(arraySize) { LinkedList<Entry<K, V>>() }

        numberOfEntries = 0

        entries.forEach {
            it.forEach { entry ->
                put(entry.key, entry.value, localEntries)
            }
        }
        entries = localEntries
    }

    private fun calculateHashCode(key: K): Int = abs(key.hashCode() % arraySize)
}