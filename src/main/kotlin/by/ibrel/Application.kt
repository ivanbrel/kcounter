package by.ibrel

import by.ibrel.kcounter.application.impl.CounterApplicationImpl
import by.ibrel.kcounter.adapter.handler.rest.configureRouting
import by.ibrel.kcounter.config.configureSwagger
import by.ibrel.kcounter.config.configureDatabase
import by.ibrel.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureSwagger()
    configureDatabase()
    configureRouting(CounterApplicationImpl())
}
