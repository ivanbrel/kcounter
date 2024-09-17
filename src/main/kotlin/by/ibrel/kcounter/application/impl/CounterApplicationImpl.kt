package by.ibrel.kcounter.application.impl

import by.ibrel.kcounter.Counter
import by.ibrel.kcounter.Counters
import by.ibrel.kcounter.application.CounterApplication
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class CounterApplicationImpl : CounterApplication {

    override suspend fun create(counterName: String, counterValue: Int): Counter =
        transaction {
            Counter.new {
                name = counterName
                count = counterValue
            }
        }

    override suspend fun read(name: String): Counter? =
        transaction {
            Counter.find { Counters.name eq name }.singleOrNull()
        }

    override suspend fun delete(name: String) {
        transaction {
            Counters.deleteWhere { Counters.name eq name }
        }
    }

    override suspend fun increment(name: String): Int =
        transaction {

            val conn = TransactionManager.current().connection
            val query = "update counters set count = count + 1 where name = ? RETURNING count"
            val statement = conn.prepareStatement(query, false)
            statement.fillParameters(listOf(Pair(VarCharColumnType(), name)))
            val result = statement.executeQuery()

            result.use {
                if (result.next()) result.getInt(1)
                else throw IllegalArgumentException("Counter with name $name not found.")
            }
        }

    override suspend fun getAll(): Map<String, Int> =
        transaction {
            Counter.all().associate { it.name to it.count }
        }
}
