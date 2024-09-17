package by.ibrel

import by.ibrel.kcounter.adapter.handler.rest.configureRouting
import by.ibrel.kcounter.config.configureDI
import by.ibrel.kcounter.config.configureDatabase
import by.ibrel.kcounter.config.configureSerialization
import by.ibrel.kcounter.config.configureStatusPage
import by.ibrel.kcounter.config.configureSwagger
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    configureMonitoring()
    configureDI()
    configureSerialization()
    configureSwagger()
    configureDatabase()
    configureRouting()
    configureStatusPage()
}
