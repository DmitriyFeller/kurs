package server.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val name: String, //name + type
    val teacher: String,
    val groups: List<String>, //number +
    val time: String,
    val cabinet: String
)