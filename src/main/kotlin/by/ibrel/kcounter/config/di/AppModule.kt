package by.ibrel.kcounter.config.di

import by.ibrel.kcounter.application.CounterApplication
import by.ibrel.kcounter.application.impl.CounterApplicationImpl
import org.koin.dsl.module

val appModule= module {
    single<CounterApplication> {
        CounterApplicationImpl()
    }
}
