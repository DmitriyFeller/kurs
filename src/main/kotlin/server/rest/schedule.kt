package server.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import server.model.Config
import server.model.Schedule
import server.repo.RepoItem
import server.repo.groupsRepo
import server.repo.scheduleRepo

fun Route.schedule() {
    route(Config.schedulePath) {
        get {
            if (!scheduleRepo.isEmpty())
                call.respond(scheduleRepo.findAll())
            else
                call.respondText("No schedules found", status = HttpStatusCode.NotFound)
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Bad schedule id",
                status = HttpStatusCode.BadRequest
            )
            val scheduleItem = scheduleRepo[id] ?: return@get call.respondText(
                    "Schedule \"$id\" not found",
                    status = HttpStatusCode.NotFound
                )
            call.respond(scheduleItem)
        }
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Bad schedule id",
                status = HttpStatusCode.BadRequest
            )
            val scheduleItem = scheduleRepo[id] ?: return@put call.respondText(
                "Schedule \"$id\" not found",
                status = HttpStatusCode.NotFound
            )
            val schedule = call.receive<Schedule>()
            val newSchedule = scheduleItem.elem.copy(
                teacher = schedule.teacher,
                cabinet = schedule.cabinet
            )
            scheduleRepo.update(scheduleItem.uuid, newSchedule)
            call.respondText("Schedule updated correctly", status = HttpStatusCode.Created)
        }
    }
    route("${Config.schedulePath}{scheduleId}/{groupId}") {
        post("/add") {
            when (val lsResult = lsParameters()) {
                is LSOk -> {
                    val oldElem = lsResult.scheduleItem.elem
                    val newElem = oldElem.copy(groups = oldElem.groups + lsResult.group)
                    scheduleRepo.update(lsResult.scheduleItem.uuid, newElem)
                    call.respond(scheduleRepo[lsResult.scheduleItem.uuid]!!)
                }
                is LSFail -> call.respondText(lsResult.text, status = lsResult.code)
            }
        }
        post("/delete") {
            when (val lsResult = lsParameters()) {
                is LSOk -> {
                    if (lsResult.group !in lsResult.scheduleItem.elem.groups)
                        return@post call.respondText(
                            "Group ${lsResult.group} not included in " +
                                    lsResult.scheduleItem.elem.name,
                            status = HttpStatusCode.NotFound
                        )
                    else {
                        val oldElem = lsResult.scheduleItem.elem
                        val newElem = oldElem.copy(groups = oldElem.groups - lsResult.group)
                        scheduleRepo.update(lsResult.scheduleItem.uuid, newElem)
                        call.respond(scheduleRepo[lsResult.scheduleItem.uuid]!!)
                    }
                }
                is LSFail -> call.respondText(lsResult.text, status = lsResult.code)
            }
        }
    }
}

private sealed interface LSResult
private class LSOk(
    val scheduleItem: RepoItem<Schedule>,
    val group: String
) : LSResult

private class LSFail(
    val text: String,
    val code: HttpStatusCode
) : LSResult

private fun PipelineContext<Unit, ApplicationCall>.lsParameters(): LSResult {
    val scheduleId = call.parameters["scheduleId"]
        ?: return LSFail("Bad scheduleId", HttpStatusCode.BadRequest)
    val groupId = call.parameters["groupId"]
        ?: return LSFail("StudentId wrong", HttpStatusCode.BadRequest)
    val scheduleItem = scheduleRepo[scheduleId]
        ?: return LSFail("Schedule \"$scheduleId\" not found", HttpStatusCode.NotFound)
    val group = groupsRepo[groupId]
        ?: return LSFail("Group \"$groupId\" not found", HttpStatusCode.NotFound)
    return LSOk(scheduleItem, group.elem.groupName)
}