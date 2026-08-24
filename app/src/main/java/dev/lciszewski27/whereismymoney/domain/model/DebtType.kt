package dev.lciszewski27.whereismymoney.domain.model

/**
 * Direction of the debt. Mirrored from the current user's perspective.
 */
enum class DebtType {
    /** Someone else owes the current user. */
    THEY_OWE_ME,
    /** The current user owes someone else. */
    I_OWE_THEM;

    val dbValue: String get() = name

    companion object {
        fun fromDb(value: String): DebtType = valueOf(value)
    }
}