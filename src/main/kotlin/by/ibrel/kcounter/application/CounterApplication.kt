package by.ibrel.kcounter.application

import by.ibrel.kcounter.Counter

interface CounterApplication {

    suspend fun create(name: String, count: Int = 0): Counter

    suspend fun read(name: String): Counter?

    suspend fun delete(name: String)

    suspend fun increment(name: String): Int

    suspend fun getAll(): Map<String, Int>

}
