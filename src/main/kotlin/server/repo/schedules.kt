package server.repo

import server.model.Schedule

val scheduleRepo = ListRepo<Schedule>()

val timeArrayTestData = listOf("8:00", "9:45", "11:30", "13:55", "15:40")

val scheduleRepoTestData = listOf(0, 1, 2).map {
    Schedule(
        lessonsRepoTestData[it].fullName,
        lessonsRepoTestData[it].teacher,
        lessonsRepoTestData[it].groups.toSet(),
        timeArrayTestData[it],
        cabinetsRepoTestData[it].address
    )
}
