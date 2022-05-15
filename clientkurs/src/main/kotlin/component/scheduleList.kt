package component

import kotlinx.css.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.*
import react.fc
import react.query.useQuery
import react.router.dom.Link
import server.model.Config.Companion.scheduleURL
import server.model.Item
import server.model.Schedule
import styled.*
import wrappers.QueryError
import wrappers.fetchText

external interface ScheduleListProps : Props {
    var scheduleList: List<Item<Schedule>>
}

fun fcScheduleList() = fc("ScheduleList") { props: ScheduleListProps ->
    h2 { +"Schedule:" }
    styledTable {
        css {
            width = 500.px
            borderCollapse = BorderCollapse.collapse
            whiteSpace = WhiteSpace.nowrap
            border = "1px solid black"
            margin = "auto"
        }
        //table headers
        tr {
            th {
                +"Time"
            }
            th {
                +"Name"
            }
            th {
                +"Cabinet"
            }
            th {
                +"Teacher"
            }
        }
        props.scheduleList.sortedBy { it.elem.time }.map {
            tr {
                td {
                    +it.elem.time
                }
                td {
                    Link {
                        +it.elem.name
                        attrs.to = "/schedule/${it.uuid}"
                    }
                }
                td {
                    +it.elem.cabinet
                }
                td {
                    +it.elem.teacher
                }
            }
        }
    }
}

@Serializable
class ClientItemSchedule(
    override val elem: Schedule,
    override val uuid: String,
    override val etag: Long
) : Item<Schedule>

fun fcContainerScheduleList() = fc("ScheduleListContainer") { _: Props ->
    val query = useQuery<String, QueryError, String, String>(
        "scheduleList",
        { fetchText(scheduleURL) }
    )

    if (query.isLoading) div { +"Loading..." }
    else if (query.isError) div { +"Query error" }
    else {
        val scheduleList: List<ClientItemSchedule> =
            Json.decodeFromString(query.data ?: "")
        child(fcScheduleList()) {
            attrs.scheduleList = scheduleList
        }
    }
}
