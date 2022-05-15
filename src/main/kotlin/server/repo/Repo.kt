package server.repo

import server.model.Item

interface Repo<E> {
    operator fun get(uuid: String): Item<E>?
    fun find(predicate: (E) -> Boolean): List<Item<E>>
    fun findAll(): List<Item<E>>
    fun create(element: E): Boolean
    fun update(uuid: String, value: E): Boolean
    fun delete(uuid: String): Boolean
    fun isEmpty(): Boolean
}
