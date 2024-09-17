package by.ibrel.kcounter.config

import by.ibrel.kcounter.Counters
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureDatabase() {

    val driverClass = environment.config.property("storage.driverClassName").getString()
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()
    val db = Database.connect(provideDataSource(jdbcUrl, driverClass))

    transaction(db) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Counters)
    }
}

private fun provideDataSource(url: String, driverClass: String): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        driverClassName = driverClass
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}
