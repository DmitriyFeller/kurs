package server.repo

import kotlinx.serialization.Serializable
import server.model.Item
import java.util.*

@Serializable
class RepoItem<E>(
    override val elem: E,
    override val uuid: String = UUID.randomUUID().toString(),
    override val etag: Long = System.currentTimeMillis()
) : Item<E>
