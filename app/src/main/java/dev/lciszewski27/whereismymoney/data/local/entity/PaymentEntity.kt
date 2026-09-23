package dev.lciszewski27.whereismymoney.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Room entity recording a single payment against a debt.
 *
 * This is the audit ledger: every full or partial settlement appends a row
 * here, so history survives even though partial payoffs settle the original
 * debt and spawn a "remaining" debt.
 *
 * [kind] is "PARTIAL", "FULL" or "SETTLE_ALL".
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = DebtEntity::class,
            parentColumns = ["id"],
            childColumns = ["debtId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["debtId"]), Index(value = ["personId"])]
)
@Serializable
data class PaymentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val debtId: String,
    val personId: String,
    val amountCents: Long,
    val currency: String = "PLN",
    val timestamp: Long = System.currentTimeMillis(),
    val kind: String = "PARTIAL" // "PARTIAL" | "FULL" | "SETTLE_ALL"
)
