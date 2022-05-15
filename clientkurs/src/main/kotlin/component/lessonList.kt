package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.*
import react.fc
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.useRef
import server.model.Config.Companion.lessonsURL
import server.model.Item
import server.model.Lesson
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

external interface LessonListProps : Props {
    var lessons: List<Item<Lesson>>
    var addLesson: (String) -> Unit
    var deleteLesson: (Int) -> Unit
}

fun fcLessonList() = fc("LessonList") { props: LessonListProps ->

    val nameRef = useRef<INPUT>()

    span {
        p {
            +"Name: "
            input {
                ref = nameRef
            }
        }
        button {
            +"Add Lesson"
            attrs.onClickFunction = {
                nameRef.current?.value?.let {
                    props.addLesson(it)
                }
            }
        }
    }

    h3 { +"Lessons" }
    ol {
        props.lessons.mapIndexed { index, lessonItem ->
            li {
                Link {
                    attrs.to = "/lessons/${lessonItem.uuid}"
                    +"${lessonItem.elem.name} \t"
                }
                button {
                    +"X"
                    attrs.onClickFunction = {
                        props.deleteLesson(index)
                    }
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
    val queryClient = useQueryClient()

    val query = useQuery<String, QueryError, String, String>(
        "lessonList",
        { fetchText(lessonsURL) }
    )

    val addLessonMutation = useMutation<Any, Any, Any, Any>(
        { lesson: Lesson ->
            axios<String>(jso {
                url = lessonsURL
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json",
                )
                data = Json.encodeToString(lesson)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("lessonList")
            }
        }
    )

    val deleteLessonMutation = useMutation<Any, Any, Any, Any>(
        { lessonItem: Item<Lesson> ->
            axios<String>(jso {
                url = "$lessonsURL/${lessonItem.uuid}"
                method = "Delete"
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("lessonList")
            }
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val items: List<ClientItemLesson> =
            Json.decodeFromString(query.data ?: "")
        child(fcLessonList()) {
            attrs.lessons = items
            attrs.addLesson = {
                addLessonMutation.mutate(Lesson(it), null)
            }
            attrs.deleteLesson = {
                deleteLessonMutation.mutate(items[it], null)
            }
        }
    }
}
