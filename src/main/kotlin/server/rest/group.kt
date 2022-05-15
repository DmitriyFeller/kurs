package server.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import server.model.Config.Companion.groupsPath
import server.model.Group
import server.repo.groupsRepo

fun Route.group() = route(groupsPath) {
    get {
        if (!groupsRepo.isEmpty())
            call.respond(groupsRepo.findAll())
        else
            call.respondText("No groups found", status = HttpStatusCode.NotFound)
    }
    post {
        val group = call.receive<Group>()
        groupsRepo.create(group)
        call.respondText("Group stored correctly", status = HttpStatusCode.Created)
    }
}
