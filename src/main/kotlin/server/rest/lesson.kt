package server.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import server.model.Config.Companion.lessonsPath
import server.repo.lessonsRepo

fun Route.lesson() {
    route(lessonsPath) {
        get {
            if (!lessonsRepo.isEmpty())
                call.respond(lessonsRepo.findAll())
            else
                call.respondText("No lessons found", status = HttpStatusCode.NotFound)
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Bad lesson id",
                status = HttpStatusCode.BadRequest
            )
            val lessonItem = lessonsRepo[id] ?: return@get call.respondText(
                "Lesson \"$id\" not found",
                status = HttpStatusCode.NotFound
            )
            call.respond(lessonItem)
        }
    }
}
