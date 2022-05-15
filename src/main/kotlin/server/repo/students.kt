package server.repo

import server.model.Config
import server.model.Student

val studentsRepo = ListRepo<Student>()

fun ListRepo<Student>.urlByUUID(uuid: String) =
    this[uuid]?.let {
        Config.studentsURL + it.uuid
    }

fun ListRepo<Student>.urlByFirstname(firstname: String) =
    this.find { it.firstname == firstname }.let {
        if (it.size == 1)
            Config.studentsURL + it.first().uuid
        else
            null
    }


val studentsRepoTestData = listOf(
    Student("Sheldon", "Cooper"),
    Student("Leonard", "Hofstadter"),
    Student("Howard", "Wolowitz"),
    Student("Penny", "Hofstadter"),
)
