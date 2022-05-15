package component

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.*
import react.fc
import react.query.useQuery
import react.router.useParams
import server.model.Config
import server.model.Item
import server.model.Lesson
import wrappers.QueryError
import wrappers.fetchText

external interface LessonProps : Props {
    var lesson: Item<Lesson>
}

fun fcLesson() = fc("Lesson") { props: LessonProps ->
    h3 { +"Lesson \"${props.lesson.elem.name}\":" }
    span {
        p {
            h4 { +"Type: " }
            +props.lesson.elem.type.lowercase()
        }
        p {
            h4 { +"Teacher: " }
            +props.lesson.elem.teacher
        }
        p {
            h4 { +"Groups:" }
            ul {
                props.lesson.elem.groups.map {
                    li {
                        +it
                    }
                }
            }
        }
    }
}

fun fcContainerLesson() = fc("ContainerLesson") { _: Props ->
    val lessonParams = useParams()
    val lessonId = lessonParams["id"] ?: "Route param error"

    val query = useQuery<String, QueryError, String, String>(
        lessonId,
        { fetchText(Config.lessonsPath + lessonId) }
    )

    if (query.isLoading) div { +"Loading..." }
    else if (query.isError) div { +"Query error" }
    else {
        val lessons: ClientItemLesson =
            Json.decodeFromString(query.data ?: "")
        child(fcLesson()) {
            attrs.lesson = lessons
        }
    }
}
