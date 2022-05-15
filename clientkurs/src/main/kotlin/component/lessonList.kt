package component

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.*
import react.fc
import react.query.useQuery
import react.router.dom.Link
import server.model.Config.Companion.lessonsURL
import server.model.Item
import server.model.Lesson
import wrappers.QueryError
import wrappers.fetchText

external interface LessonListProps : Props {
    var lessons: List<Item<Lesson>>
}

fun fcLessonList() = fc("LessonList") { props: LessonListProps ->
    h3 { +"Lessons:" }
    ol {
        props.lessons.map {
            li {
                Link {
                    attrs.to = "/lessons/${it.uuid}"
                    +it.elem.fullName
                }
            }
        }
    }
}

@Serializable
class ClientItemLesson(
    override val elem: Lesson,
    override val uuid: String,
    override val etag: Long
) : Item<Lesson>

fun fcContainerLessonList() = fc("LessonListContainer") { _: Props ->
    val query = useQuery<String, QueryError, String, String>(
        "lessonList",
        { fetchText(lessonsURL) }
    )

    if (query.isLoading) div { +"Loading..." }
    else if (query.isError) div { +"Query error" }
    else {
        val items: List<ClientItemLesson> =
            Json.decodeFromString(query.data ?: "")
        child(fcLessonList()) {
            attrs.lessons = items
        }
    }
}
