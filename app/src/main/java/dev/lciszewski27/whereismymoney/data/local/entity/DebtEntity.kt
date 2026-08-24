package dev.lciszewski27.whereismymoney.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Room entity representing a single debt/IOU transaction.
 *
 * Amount is stored as [Long] in minor units (cents) — always use
 * the smallest denomination of the chosen currency.
 *
 * [type] is "THEY_OWE_ME" or "I_OWE_THEM".
 */
@Entity(
    tableName = "debts",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
@Serializable
data class DebtEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val personId: String,
    val amountCents: Long,
    val currency: String = "PLN",
    val type: String = "THEY_OWE_ME",  // "THEY_OWE_ME" | "I_OWE_THEM"
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val dueDateMillis: Long? = null,
    val isSettled: Boolean = false
)
