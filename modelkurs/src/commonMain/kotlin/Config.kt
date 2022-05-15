package server.model

class Config {
    companion object {
        const val serverDomain = "localhost"
        const val serverPort = 8000
        const val serverApi = "1"
        const val serverUrl = "http://$serverDomain:$serverPort/"
        const val pathPrefix = "api$serverApi/"

        const val lessonsPath = "${pathPrefix}lessons/"
        const val lessonsURL = "$serverUrl$lessonsPath"

        const val groupsPath = "${pathPrefix}groups/"
        const val groupsURL = "$serverUrl$groupsPath"

        const val cabinetsPath = "${pathPrefix}cabinets/"
        const val cabinetsURL = "$serverUrl$cabinetsPath"

        const val schedulePath = "${pathPrefix}schedule/"
        const val scheduleURL = "$serverUrl$schedulePath"
    }
}
