package server.repo

import server.model.Cabinet

val cabinetsRepo = ListRepo<Cabinet>()

val cabinetsRepoTestData = listOf(
    Cabinet("1", 101),
    Cabinet("1", 102),
    Cabinet("1", 201),
    Cabinet("1", 202),
    Cabinet("2", 201),
    Cabinet("2", 202),
    Cabinet("2", 203),
    Cabinet("5s", 101),
    Cabinet("5s", 102),
)
