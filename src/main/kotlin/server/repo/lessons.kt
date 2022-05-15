package server.repo

import server.model.Lesson

val lessonsRepo = ListRepo<Lesson>()

val lessonsRepoTestData = listOf(
    Lesson("Math"),
    Lesson("Phys"),
    Lesson("Story"),
)
