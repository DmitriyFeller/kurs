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
import server.model.Item
import server.model.Cabinet
import server.model.Config.Companion.cabinetsURL
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

external interface CabinetListProps : Props {
    var cabinets: List<Item<Cabinet>>
    var addCabinet: (String, Int) -> Unit
    var deleteCabinet: (Int) -> Unit
}

fun fcCabinetList() = fc("CabinetList") { props: CabinetListProps ->
    val buildingRef = useRef<INPUT>()
    val numberRef = useRef<INPUT>()

    span {
        p {
            input {
                ref = buildingRef
                attrs.placeholder = "building"
            }
            input {
                ref = numberRef
                attrs.placeholder = "number"
            }
            button {
                +"add cabinet"
                attrs.onClickFunction = {
                    buildingRef.current?.value?.let { building ->
                        numberRef.current?.value?.let { number ->
                            val numberInt = number.toIntOrNull()
                            if (numberInt != null)
                                props.addCabinet(building, numberInt)
                        }
                    }
                }
            }
        }
    }

    h3 { +"Cabinets:" }
    ol {
        props.cabinets.mapIndexed { i, c ->
            li {
                +c.elem.address
                +" "
                button {
                    +"X"
                    attrs.onClickFunction = {
                        props.deleteCabinet(i)
                    }
                }
            }
        }
    }
}

@Serializable
class ClientItemCabinet(
    override val elem: Cabinet,
    override val uuid: String,
    override val etag: Long
) : Item<Cabinet>

fun fcContainerCabinetList() = fc("CabinetListContainer") { _: Props ->
    val queryClient = useQueryClient()

    val query = useQuery<String, QueryError, String, String>(
        "cabinetList",
        { fetchText(cabinetsURL) }
    )

    val addCabinetMutation = useMutation<Any, Any, Cabinet, Any>(
        { cabinet ->
            axios<String>(jso {
                url = cabinetsURL
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json"
                )
                data = Json.encodeToString(cabinet)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("cabinetList")
            }
        }
    )

    val deleteCabinetMutation = useMutation<Any, Any, Any, Any>(
        { cabinetItem: Item<Cabinet> ->
            axios<String>(jso {
                url = "$cabinetsURL${cabinetItem.uuid}"
                method = "Delete"
                headers = json(
                    "Content-Type" to "application/json"
                )
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("cabinetList")
            }
        }
    )

    if (query.isLoading) div { +"Loading..." }
    else if (query.isError) div { +"Query error" }
    else {
        val cabinets: List<ClientItemCabinet> =
            Json.decodeFromString(query.data ?: "")
        child(fcCabinetList()) {
            attrs.cabinets = cabinets
            attrs.addCabinet = { building, number ->
                addCabinetMutation.mutate(Cabinet(building, number), null)
            }
            attrs.deleteCabinet = {
                deleteCabinetMutation.mutate(cabinets[it], null)
            }
        }
    }
}
