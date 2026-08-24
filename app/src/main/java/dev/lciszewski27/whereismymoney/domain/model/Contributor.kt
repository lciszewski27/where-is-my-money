package dev.lciszewski27.whereismymoney.domain.model

import kotlinx.serialization.Serializable

/**
 * A contributor to the Where Is My Money project.
 * Loaded from a bundled JSON resource.
 */
@Serializable
data class Contributor(
    val name: String,
    val imageUrl: String = "",
    val role: String = "Contributor",
    val githubUrl: String = ""
)