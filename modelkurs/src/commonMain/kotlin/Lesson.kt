package server.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val name: String,
    val type: String,
    val teacher: String,
    val groups: List<String>
) {
    val fullName: String
        get() = "$name (${type.lowercase()})"
}
