package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.SELECT
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.useParams
import server.model.*
import server.model.Config.Companion.scheduleURL
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

external interface ScheduleProps : Props {
    var schedule: Item<Schedule>
    var cabinets: List<Item<Cabinet>>
    var groups: List<Item<Group>>
    var updateTeacher: (String) -> Unit
    var updateCabinet: (String) -> Unit
    var addGroup: (String) -> Unit
    var deleteGroup: (String) -> Unit
}

interface MySelect { val value: String }

fun fcSchedule() = fc("ScheduleList") { props: ScheduleProps ->
    h2 { +"Edit \"${props.schedule.elem.name}\"" }
    val scheduleTeacherRef = useRef<INPUT>()
    val scheduleCabinetRef = useRef<SELECT>()
    val scheduleAddGroupRef = useRef<SELECT>()
    val scheduleDeleteGroupRef = useRef<SELECT>()

    val (teacher, setTeacher) = useState(props.schedule.elem.teacher)
    fun onInputEdit(setter: StateSetter<String>, ref: MutableRefObject<INPUT>) = { _: Event ->
        setter(ref.current?.value ?: "ERROR!")
    }

    div {
        p {
            +"Time [read-only]: "
            +props.schedule.elem.time
        }
        p {
            +"Teacher: "
            input {
                ref = scheduleTeacherRef
                attrs.value = teacher
                attrs.onChangeFunction = onInputEdit(setTeacher, scheduleTeacherRef)
            }
            button {
                +"update teacher"
                attrs.onClickFunction = {
                    scheduleTeacherRef.current?.value?.let { teacher ->
                        if (teacher.isNotBlank())
                            props.updateTeacher(teacher)
                    }
                }
            }
        }
        p {
            +"Cabinet: "
            select {
                ref = scheduleCabinetRef
                props.cabinets
                    .sortedBy { it.elem.address != props.schedule.elem.cabinet }.map {
                    option {
                        attrs.value = it.elem.address
                        +it.elem.address
                    }
                }
            }
            button {
                +"update cabinet"
                attrs.onClickFunction = {
                    val select = scheduleCabinetRef.current.unsafeCast<MySelect>()
                    if (select.value.isNotBlank())
                        props.updateCabinet(select.value)
                }
            }
        }
        p {
            +"Add group: "
            select {
                ref = scheduleAddGroupRef
                props.groups.map {
                    if (!props.schedule.elem.groups.contains(it.elem.groupName))
                        option {
                            attrs.value = it.uuid
                            +it.elem.groupName
                        }
                }
            }
            button {
                +"add"
                attrs.onClickFunction = {
                    val select = scheduleAddGroupRef.current.unsafeCast<MySelect>()
                    if (select.value.isNotBlank())
                        props.addGroup(select.value)
                }
            }
        }
        p {
            +"Delete group: "
            select {
                ref = scheduleDeleteGroupRef
                props.groups.map {
                    if (props.schedule.elem.groups.contains(it.elem.groupName))
                        option {
                            attrs.value = it.uuid
                            +it.elem.groupName
                        }
                }
            }
            button {
                +"del"
                attrs.onClickFunction = {
                    val select = scheduleDeleteGroupRef.current.unsafeCast<MySelect>()
                    if (select.value.isNotBlank())
                        props.deleteGroup(select.value)
                }
            }
        }
        p {
            +"Groups: "
            ul {
                props.schedule.elem.groups.map {
                    li {
                        +it
                    }
                }
            }
        }
    }
}

fun fcContainerSchedule() = fc("ScheduleContainer") { _: Props ->
    val queryClient = useQueryClient()

    val scheduleParams = useParams()
    val scheduleId = scheduleParams["id"] ?: "Route param error"

    val querySchedule = useQuery<String, QueryError, String, String>(
        scheduleId, { fetchText(scheduleURL + scheduleId) }
    )

    val queryCabinets = useQuery<String, QueryError, String, String>(
        "cabinetList", { fetchText(Config.cabinetsURL) }
    )

    val queryGroups = useQuery<String, QueryError, String, String>(
        "groupList", { fetchText(Config.groupsURL) }
    )

    val updateScheduleMutation = useMutation<Any, Any, Schedule, Any>(
        { schedule ->
        axios<String>(jso {
            url = "$scheduleURL$scheduleId"
            method = "Put"
            headers = json(
                "Content-Type" to "application/json"
            )
            data = Json.encodeToString(schedule)
        })
    },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(scheduleId)
                queryClient.invalidateQueries<Any>("scheduleList")
            }
        }
    )

    val addGroupMutation = useMutation<Any, Any, String, Any>(
        { groupId ->
        axios<String>(jso {
            url = "$scheduleURL$scheduleId/groups/$groupId/add"
            method = "Post"
            headers = json(
                "Content-Type" to "application/json"
            )
        })
    },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(scheduleId)
                queryClient.invalidateQueries<Any>("scheduleList")
            }
        }
    )

    val deleteGroupMutation = useMutation<Any, Any, String, Any>(
        { groupId ->
        axios<String>(jso {
            url = "$scheduleURL$scheduleId/groups/$groupId/delete"
            method = "Post"
            headers = json(
                "Content-Type" to "application/json"
            )
        })
    },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(scheduleId)
                queryClient.invalidateQueries<Any>("scheduleList")
            }
        }
    )

    if (querySchedule.isLoading or queryCabinets.isLoading or queryGroups.isLoading)
        div { +"Loading..." }
    else if (querySchedule.isError or queryCabinets.isError or queryGroups.isError)
        div { +"Query error" }
    else {
        val schedule: ClientItemSchedule =
            Json.decodeFromString(querySchedule.data ?: "")
        val cabinets: List<ClientItemCabinet> =
            Json.decodeFromString(queryCabinets.data ?: "")
        val groups: List<ClientItemGroup> =
            Json.decodeFromString(queryGroups.data ?: "")
        child(fcSchedule()) {
            attrs.schedule = schedule
            attrs.cabinets = cabinets
            attrs.groups = groups
            attrs.updateTeacher = { teacher ->
                updateScheduleMutation.mutate(
                    Schedule(
                        name=schedule.elem.name,
                        teacher=teacher,
                        cabinet=schedule.elem.cabinet
                    ),
                    null
                )
            }
            attrs.updateCabinet = { cabinet ->
                updateScheduleMutation.mutate(
                    Schedule(
                        name=schedule.elem.name,
                        teacher=schedule.elem.teacher,
                        cabinet=cabinet
                    ),
                    null
                )
            }
            attrs.addGroup = {
                addGroupMutation.mutate(it, null)
            }
            attrs.deleteGroup = {
                deleteGroupMutation.mutate(it, null)
            }
        }
    }
}
