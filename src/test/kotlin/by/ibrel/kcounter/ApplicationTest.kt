package by.ibrel.kcounter

import by.ibrel.kcounter.Counter
import by.ibrel.kcounter.adapter.handler.rest.configureRouting
import by.ibrel.kcounter.application.CounterApplication
import by.ibrel.kcounter.config.configureSerialization
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class ApplicationTest {

    @Test
    internal fun `get counter, counter exists, should return count`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()
        val counter = mockk<Counter>()

        coEvery { counterApplication.read(testCounterName) } returns counter

        every { counter.count } returns 1

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.get("/counters/$testCounterName").apply {

            //then
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("1", bodyAsText())
        }
    }

    @Test
    internal fun `get counter, counter not exists, should return not found`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()
        val counter = mockk<Counter>()

        coEvery { counterApplication.read(testCounterName) } returns null

        every { counter.count } returns 1

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.get("/counters/$testCounterName").apply {

            //then
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    internal fun `create counter, counter is uniq, should return created`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()
        val counter = mockk<Counter>()

        coEvery { counterApplication.create(testCounterName) } returns counter

        every { counter.count } returns 1
        every { counter.name } returns testCounterName

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.post("/counters") {
            contentType(ContentType.Application.Json)

            setBody("{\"name\": \"$testCounterName\", \"count\": 0}")
        }.apply {

            //then
            assertEquals(HttpStatusCode.Created, status)
            assertEquals(testCounterName, bodyAsText())
        }
    }

    @Test
    internal fun `create counter, counter is not uniq, should return bad request`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()

        coEvery { counterApplication.create(testCounterName) } throws IllegalArgumentException()

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.post("/counters") {
            contentType(ContentType.Application.Json)

            setBody("{\"name\": \"$testCounterName\", \"count\": 0}")
        }.apply {

            //then
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    internal fun `increment counter, counter is inc, should return new counter`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()

        coEvery { counterApplication.increment(testCounterName) } returns 2

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.patch("/counters/$testCounterName").apply {

            //then
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("2", bodyAsText())
        }
    }

    @Test
    internal fun `get all counters, counters are exists, should return map`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()

        coEvery { counterApplication.getAll() } returns mapOf(testCounterName to 3)

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.get("/counters").apply {

            //then
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(buildJsonObject { put(testCounterName, 3) }.toString(), bodyAsText())
        }
    }

    @Test
    internal fun `delete counter, counter is exists, should delete`() = testApplication {

        //given
        val testCounterName = "test"

        val counterApplication = mockk<CounterApplication>()

        coEvery { counterApplication.delete(testCounterName) } returns Unit

        application {
            configureRouting(counterApplication)
            configureSerialization()
        }

        //when
        client.delete("/counters/$testCounterName").apply {

            //then
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
