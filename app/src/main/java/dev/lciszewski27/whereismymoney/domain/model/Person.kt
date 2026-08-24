package dev.lciszewski27.whereismymoney.domain.model

/**
 * Domain model for a person/contact.
 */
data class Person(
    val id: String,
    val name: String,
    val colorSeed: Long,
    val createdAt: Long,
    val balanceCents: Long = 0L,
    val currency: String = ""
) {
    companion object {
        /** User-facing "create a new contact" placeholder. */
        val EMPTY = Person(
            id = "",
            name = "",
            colorSeed = 0L,
            createdAt = 0L
        )
    }
}