package server.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val number: Byte, //29
    val specialty: Char //'m'
) {
    val groupName: String
        get() = "$number$specialty"
}
