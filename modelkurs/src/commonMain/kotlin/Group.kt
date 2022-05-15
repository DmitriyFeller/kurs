package server.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val number: Int, //29 (potok)
    val specialty: Char //'m'
) {
    val groupName: String
        get() = "$number$specialty"
}
