package server.model

import kotlinx.serialization.Serializable

@Serializable
data class Cabinet(
    val building: String, //may contain letters
    val number: Int //always a number
) {
    val address: String
        get() = "$building-$number"
}