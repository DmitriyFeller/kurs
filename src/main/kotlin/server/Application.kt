package server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import server.model.Config
import server.repo.lessonsRepo
import server.repo.lessonsRepoTestData
import server.repo.studentsRepo
import server.repo.studentsRepoTestData
import server.rest.lesson
import server.rest.student

fun main() {
    embeddedServer(
        Netty,
        port = Config.serverPort,
        host = Config.serverDomain,
        watchPaths = listOf("classes", "resources")
    ) {
        main()
    }.start(wait = true)
}

fun Application.main(test: Boolean = true) {
    if(test) {
        studentsRepoTestData.forEach { studentsRepo.create(it) }
        lessonsRepoTestData.forEach { lessonsRepo.create(it) }
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        index()
        student()
        lesson()
    }
}