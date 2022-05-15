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
import react.useRef
import server.model.Config.Companion.groupsURL
import server.model.Item
import server.model.Group
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json
import kotlin.math.abs

external interface GroupListProps : Props {
    var groups: List<Item<Group>>
    var addGroup: (Int, Char) -> Unit
}

fun fcGroupList() = fc("GroupList") { props: GroupListProps ->
    val numberRef = useRef<INPUT>()
    val specialtyRef = useRef<INPUT>()

    span {
        p {
            input {
                ref = numberRef
                attrs.placeholder = "number"
            }
            input {
                ref = specialtyRef
                attrs.placeholder = "specialty"
            }
            button {
                +"add group"
                attrs.onClickFunction = {
                    numberRef.current?.value?.let { num ->
                        val numInt = num.toIntOrNull()
                        if (numInt != null)
                            specialtyRef.current?.value?.let { spec ->
                                if (spec.isNotBlank())
                                    props.addGroup(abs(numInt), spec[0])
                                //take absolute value from converted number
                                //&
                                //always take first character from
                                //specialtyRef input box
                            }
                    }
                }
            }
        }
    }

    h3 { +"Groups:" }
    ol {
        props.groups.map {
            li {
                +it.elem.groupName
            }
        }
    }
}

@Serializable
class ClientItemGroup(
    override val elem: Group,
    override val uuid: String,
    override val etag: Long
) : Item<Group>

fun fcContainerGroupList() = fc("GroupListContainer") { _: Props ->
    val queryClient = useQueryClient()

    val query = useQuery<String, QueryError, String, String>(
        "groupList",
        { fetchText(groupsURL) }
    )

    val addGroupMutation = useMutation<Any, Any, Group, Any>(
        { group ->
            axios<String>(jso {
                url = groupsURL
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json"
                )
                data = Json.encodeToString(group)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("groupList")
            }
        }
    )

    if (query.isLoading) div { +"Loading..." }
    else if (query.isError) div { +"Query error" }
    else {
        val groups: List<ClientItemGroup> =
            Json.decodeFromString(query.data ?: "")
        child(fcGroupList()) {
            attrs.groups = groups
            attrs.addGroup = { num, spec ->
                addGroupMutation.mutate(Group(num, spec), null)
            }
        }
    }
}
