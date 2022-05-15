package server.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import server.model.Cabinet
import server.model.Config
import server.repo.cabinetsRepo

fun Route.cabinet() = route(Config.cabinetsPath) {
    get {
        if (!cabinetsRepo.isEmpty())
            call.respond(cabinetsRepo.findAll())
        else
            call.respondText("No cabinets found", status = HttpStatusCode.NotFound)
    }
    post {
        val cabinet = call.receive<Cabinet>()
        cabinetsRepo.create(cabinet)
        call.respondText("Cabinet stored correctly", status = HttpStatusCode.Created)
    }
    delete("{id}") {
        val id = call.parameters["id"] ?: return@delete call
            .respondText("Bad cabinet id", status = HttpStatusCode.BadRequest)
        if (cabinetsRepo.delete(id))
            call.respondText("Cabinet removed correctly", status = HttpStatusCode.OK)
        else
            call.respondText("Cabinet \"$id\" not found", status = HttpStatusCode.NotFound)
    }
}
