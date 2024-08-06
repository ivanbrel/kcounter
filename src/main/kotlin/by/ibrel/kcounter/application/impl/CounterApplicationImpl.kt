package by.ibrel.kcounter.application.impl

import by.ibrel.kcounter.Counter
import by.ibrel.kcounter.Counters
import by.ibrel.kcounter.application.CounterApplication
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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
            Counters.update({ Counters.name eq name }) {
                with(SqlExpressionBuilder) {
                    it.update(count, count + 1)
                }
            }
            commit()
            Counter.find { Counters.name eq name }.single().count
        }

    override suspend fun getAll(): Map<String, Int> =
        transaction {
            Counter.all().associate { it.name to it.count }
        }
}
