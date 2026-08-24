package dev.lciszewski27.whereismymoney.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Room entity representing a person/contact.
 * [colorSeed] is the avatar's color seed for consistent generated tonality.
 */
@Entity(tableName = "persons")
@Serializable
data class PersonEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val colorSeed: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)