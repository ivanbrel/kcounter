package by.ibrel.kcounter.config

import by.ibrel.kcounter.config.di.appModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin

fun Application.configureDI(){
    install(Koin){
        modules(appModule)
    }
}
