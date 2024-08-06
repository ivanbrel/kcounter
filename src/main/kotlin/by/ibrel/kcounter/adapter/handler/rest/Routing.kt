package by.ibrel.kcounter.adapter.handler.rest

import by.ibrel.kcounter.ExposedCounter
import by.ibrel.kcounter.application.CounterApplication
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting(counterApplication: CounterApplication) {

    routing {

        route("/counters") {

            post() {
                val counter = call.receive<ExposedCounter>()
                try {
                    val id = counterApplication.create(counter.name, counter.count).name
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get() {
                try {
                    val allValues = counterApplication.getAll()
                    call.respond(HttpStatusCode.OK, allValues)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            get("/{name}") {
                val name =
                    call.parameters["name"] ?: throw IllegalArgumentException("Required path param 'name' is absent.")
                try {
                    val counterValue = counterApplication.read(name)
                        ?: throw IllegalArgumentException("Required path param 'name' is absent.")
                    call.respond(HttpStatusCode.OK, counterValue.count)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            patch("/{name}") {
                val counterName =
                    call.parameters["name"] ?: throw IllegalArgumentException("Required path param 'name' is absent.")
                val updatedCount = counterApplication.increment(counterName)
                call.respond(HttpStatusCode.OK, updatedCount)
            }

            delete("/{name}") {
                val counterName =
                    call.parameters["name"] ?: throw IllegalArgumentException("Required path param 'name' is absent.")
                counterApplication.delete(counterName)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
