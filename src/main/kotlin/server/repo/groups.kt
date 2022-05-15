package server.repo

import server.model.Group

val groupsRepo = ListRepo<Group>()

val groupsRepoTestData = listOf(
    Group(29, 'a'),
    Group(29, 'b'),
    Group(29, 'v'),
    Group(29, 'z'),
    Group(29, 'i'),
    Group(29, 'm'),
    Group(28, 'a'),
    Group(28, 'b'),
    Group(28, 'v')
)
