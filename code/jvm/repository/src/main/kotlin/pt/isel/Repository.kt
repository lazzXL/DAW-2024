package pt.isel

import java.util.UUID

interface Repository<T> {
    fun findById(id: UInt): T?
    fun findAll(): List<T>
    fun save(entity: T)
    fun deleteById(id: UInt)
}