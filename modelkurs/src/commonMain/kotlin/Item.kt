package server.model

interface Item<E> {
    val elem: E
    val uuid: String
    val etag: Long
}