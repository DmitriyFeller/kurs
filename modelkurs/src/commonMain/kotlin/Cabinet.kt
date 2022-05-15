package server.model

import kotlinx.serialization.Serializable

@Serializable
data class Cabinet(
    val number: Int, //always a number
    val building: String //may contain letters
) {
    val address: String
        get() = "$building-$number"
}