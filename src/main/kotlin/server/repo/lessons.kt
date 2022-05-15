package server.repo

import server.model.Lesson

val lessonsRepo = ListRepo<Lesson>()

val lessonsRepoTestData = listOf(
    Lesson("Math", "Lecture", "Nephthys Santos",
        listOf("29a", "29b", "29v", "29m", "29z")),
    Lesson("Phys", "Lab", "Liam Mason",
        listOf("29m", "29z", "29a", "29v", "29i")),
    Lesson("Story", "Practice", "Novella Franzese",
        listOf("28a, 28b", "28v"))
)
