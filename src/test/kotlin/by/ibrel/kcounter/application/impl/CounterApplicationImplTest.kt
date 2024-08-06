package by.ibrel.kcounter.application.impl

import by.ibrel.kcounter.Counter
import by.ibrel.kcounter.Counters
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class CounterApplicationImplTest {

    private lateinit var testSubject: CounterApplicationImpl

    @BeforeEach
    internal fun setUp() {
        testSubject = CounterApplicationImpl()
    }

    companion object {

        @JvmStatic
        @BeforeAll
        internal fun setUpDb() {
            testApplication {

                Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

                transaction {
                    addLogger(StdOutSqlLogger)
                    SchemaUtils.create(Counters)
                }
            }
        }

        @JvmStatic
        @AfterAll
        internal fun tearDown() {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.drop(Counters)
            }
        }
    }


    @Test
    internal fun `create counter, should create`() = testApplication {

        //given
        val name = "test"
        val count = 0

        //when
        runBlocking {
            val result = testSubject.create(name, count)

            //then
            Assertions.assertEquals(name, result.name)
            Assertions.assertEquals(count, result.count)
        }
    }

    @Test
    internal fun `inc counter, should inc`() = testApplication {

        //given
        val name = "test2"
        val count = 5

        //when
        runBlocking {
            testSubject.create(name, count)
            val result = testSubject.increment(name)

            //then
            Assertions.assertEquals(6, result)
        }
    }

    @Test
    internal fun `delete counter, should delete`() = testApplication {

        //given
        val name = "test3"
        val count = 5

        //when
        runBlocking {
            testSubject.create(name, count)
            testSubject.delete(name)

            //then
            transaction {
                Assertions.assertEquals(0, Counter.find(Counters.name eq name).count())
            }
        }
    }

    @Test
    internal fun `get counter, counter exists, should return`() = testApplication {

        //given
        val name = "test3"
        val count = 5

        //when
        runBlocking {
            val created = testSubject.create(name, count)
            val result = testSubject.read(name)

            //then
            Assertions.assertEquals(created.name, result?.name)
        }
    }
}
