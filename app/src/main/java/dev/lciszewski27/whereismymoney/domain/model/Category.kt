package dev.lciszewski27.whereismymoney.domain.model

/**
 * Domain model representing a debt category.
 */
data class Category(
    val id: String,
    val name: String,
    val colorSeed: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)