package server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import server.model.Config
import server.repo.*
import server.rest.cabinet
import server.rest.lesson
import server.rest.group
import server.rest.schedule

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
        groupsRepoTestData.forEach { groupsRepo.create(it) }
        cabinetsRepoTestData.forEach { cabinetsRepo.create(it) }
        lessonsRepoTestData.forEach { lessonsRepo.create(it) }
        scheduleRepoTestData.forEach { scheduleRepo.create(it) }
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        index()
        group()
        cabinet()
        lesson()
        schedule()
    }
}