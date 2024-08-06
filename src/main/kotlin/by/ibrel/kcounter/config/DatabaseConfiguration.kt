package by.ibrel.kcounter.config

import by.ibrel.kcounter.Counters
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {

    Database.connect(
        "jdbc:postgresql://localhost:54340/postgres",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    //todo to migration
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Counters)
    }
}
