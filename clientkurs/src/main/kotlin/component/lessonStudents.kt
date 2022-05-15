package component

import kotlinext.js.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.div
import react.dom.h4
import react.dom.li
import react.dom.ol
import react.fc
import react.query.useQuery
import server.model.Config
import server.model.Item
import server.model.Student
import wrappers.QueryError
import wrappers.fetchText
import kotlin.js.json

external interface LessonStudentsProps : Props {
    var students: List<Item<Student>>
}

fun fcLessonStudents() = fc("LessonStudents") { props: LessonStudentsProps ->
    if (props.students.isNotEmpty()) {
        h4 { +"Students" }
        ol {
            props.students.map {
                li { +it.elem.fullname }
            }
        }
    }
}

external interface ContainerLessonStudentsProps : Props {
    var studentUUIDS: List<String>
}

fun fcContainerLessonStudents() = fc("ContainerLessonStudents") { props: ContainerLessonStudentsProps ->

    val query = useQuery<String, QueryError, String, String>(
        "lessonStudents",
        {
            fetchText(
                Config.studentsURL + "byUUIDs",
                jso {
                    method = "POST"
                    body = Json.encodeToString(props.studentUUIDS)
                    headers = json("Content-Type" to "application/json")
                }
            )
        }
    )

    if (query.isLoading or query.isLoading) div { +"Loading .." }
    else if (query.isError or query.isError) div { +"Error!" }
    else {
        val students: List<ClientItemStudent> =
            Json.decodeFromString(query.data ?: "")
        child(fcLessonStudents()) {
            attrs.students = students
        }
    }
}

