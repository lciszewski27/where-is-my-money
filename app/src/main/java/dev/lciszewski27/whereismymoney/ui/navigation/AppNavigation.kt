package dev.lciszewski27.whereismymoney.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using @Serializable.
 */
sealed interface Route {
    @Serializable
    data object Dashboard : Route

    @Serializable
    data class PersonDetail(val personId: String) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Contributors : Route
}