package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.SELECT
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.*
import react.fc
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.useParams
import react.useRef
import server.model.Config
import server.model.Item
import server.model.Lesson
import server.model.Student
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

external interface LessonProps : Props {
    var lesson: Item<Lesson>
    var allStudents: List<Item<Student>>
    var studentUUIDs: List<String>
    var updateLessonName: (String) -> Unit
    var addStudent: (String) -> Unit
}

interface MySelect {
    val value: String
}

fun fcLesson() = fc("Lesson") { props: LessonProps ->
    val newNameRef = useRef<INPUT>()
    val selectRef = useRef<SELECT>()

    h3 { +props.lesson.elem.name }

    div {
        input {
            ref = newNameRef
        }
        button {
            +"Update lesson name"
            attrs.onClickFunction = {
                newNameRef.current?.value?.let {
                    props.updateLessonName(it)
                }
            }
        }
    }
    div {
        select {
            ref = selectRef
            props.allStudents.map {
                val student = Student(it.elem.firstname, it.elem.surname)
                option {
                    +student.fullname
                    attrs.value = it.uuid
                }
            }
        }
        button {
            +"Add student"
            attrs.onClickFunction = {
                val select = selectRef.current.unsafeCast<MySelect>()
                val uuid = select.value
                props.addStudent(uuid)
            }
        }
    }

    child(fcContainerLessonStudents()) {
        attrs.studentUUIDS = props.studentUUIDs
    }
}

fun fcContainerLesson() = fc("ContainerLesson") { _: Props ->
    val lessonParams = useParams()
    val queryClient = useQueryClient()

    val lessonId = lessonParams["id"] ?: "Route param error"

    val queryLesson = useQuery<String, QueryError, String, String>(
        lessonId,
        { fetchText(Config.lessonsPath + lessonId) }
    )

    val queryStudents = useQuery<String, QueryError, String, String>(
        "studentList",
        { fetchText(Config.studentsURL) }
    )

    val updateLessonNameMutation = useMutation<Any, Any, String, Any>(
        { name ->
            axios<String>(jso {
                url = "${Config.lessonsURL}/$lessonId/name"
                method = "Put"
                headers = json(
                    "Content-Type" to "application/json",
                )
                data = Json.encodeToString(Lesson(name))
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(lessonId)
                queryClient.invalidateQueries<Any>("lessonList")
            }
        }
    )

    val addStudentMutation = useMutation<Any, Any, String, Any>(
        { studentId ->
            axios<String>(jso {
                url = "${Config.lessonsURL}/$lessonId/students/$studentId"
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json",
                )
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(lessonId)
                queryClient.invalidateQueries<Any>("lessonStudents")
            }
        }
    )

    if (queryLesson.isLoading or queryStudents.isLoading) div { +"Loading .." }
    else if (queryLesson.isError or queryStudents.isError) div { +"Error!" }
    else {
        val lessonItem: ClientItemLesson =
            Json.decodeFromString(queryLesson.data ?: "")
        val studentsUUIDs = lessonItem.elem.students.map {
            it.substringAfterLast("/")
        }
        val studentItems: List<ClientItemStudent> =
            Json.decodeFromString<List<ClientItemStudent>>(queryStudents.data ?: "")
                .filter { it.uuid !in studentsUUIDs }
        child(fcLesson()) {
            attrs.lesson = lessonItem
            attrs.allStudents = studentItems
            attrs.studentUUIDs = studentsUUIDs
            attrs.updateLessonName = {
                updateLessonNameMutation.mutate(it, null)
            }
            attrs.addStudent = {
                addStudentMutation.mutate(it, null)
            }
        }
    }
}



