package by.ibrel.kcounter

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Counters : IntIdTable() {
    val name = varchar("name", 10).uniqueIndex()
    val count = integer("count")
}

class Counter(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Counter>(Counters)

    var name by Counters.name
    var count by Counters.count

}

@Serializable
data class ExposedCounter(val name: String, val count: Int) {
    constructor(entity: Counter) : this(entity.name, entity.count)
}
