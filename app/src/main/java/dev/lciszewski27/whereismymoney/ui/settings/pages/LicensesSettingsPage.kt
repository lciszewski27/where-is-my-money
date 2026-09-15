package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mikepenz.aboutlibraries.util.withContext as withAboutLibrariesJson

/**
 * Open Source Licenses page.
 *
 * The data comes from `aboutlibraries.json`, which the AboutLibraries Gradle
 * plugin regenerates at build time from the current dependency graph
 * (see `app/build.gradle.kts`), so new/removed dependencies appear here
 * automatically — no hand-maintained list. Rows are Material 3 Expressive
 * [SegmentedListItem]s grouped into one connected list, matching the
 * contributors list on the About page.
 */
@Composable
internal fun LicensesSettingsPage(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var reloadToken by remember { mutableIntStateOf(0) }

    val state by produceState<LicensesUiState>(
        initialValue = LicensesUiState.Loading,
        key1 = reloadToken,
        key2 = context.applicationContext,
    ) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                Libs.Builder()
                    .withAboutLibrariesJson(context.applicationContext)
                    .build()
            }.fold(
                onSuccess = { libs ->
                    if (libs.libraries.isEmpty()) LicensesUiState.Empty
                    else LicensesUiState.Loaded(libs)
                },
                onFailure = { LicensesUiState.Error }
            )
        }
    }

    when (val current = state) {
        LicensesUiState.Loading -> LicensesLoading(modifier = modifier)
        LicensesUiState.Error -> LicensesError(
            modifier = modifier,
            onRetry = { reloadToken++ },
        )

        LicensesUiState.Empty -> LicensesEmpty(modifier = modifier)
        is LicensesUiState.Loaded -> LicensesList(
            libs = current.libs,
            modifier = modifier,
        )
    }
}

private sealed interface LicensesUiState {
    data object Loading : LicensesUiState
    data object Error : LicensesUiState
    data object Empty : LicensesUiState
    data class Loaded(val libs: Libs) : LicensesUiState
}

/** Loaded state: expressive summary header + connected segmented rows. */
@Composable
private fun LicensesList(
    libs: Libs,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = MoneySpacing.md,
            end = MoneySpacing.md,
            top = MoneySpacing.sm,
            bottom = MoneySpacing.xl,
        ),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Summary header scrolls away with the list — it is list content,
        // not a sticky top bar.
        items(
            items = listOf(Unit),
            key = { "licenses-header" },
        ) {
            LicensesHeader(
                count = libs.libraries.size,
                modifier = Modifier.padding(bottom = MoneySpacing.sm),
            )
        }
        itemsIndexed(
            items = libs.libraries,
            key = { _, library -> library.uniqueId },
        ) { index, library ->
            LicenseRow(
                library = library,
                index = index,
                count = libs.libraries.size,
                onClick = { openLibraryPage(uriHandler, library) },
            )
        }
    }
}

/**
 * Opens the library's license in the browser: the license URL first,
 * falling back to the library website when the scan found no license link.
 */
private fun openLibraryPage(
    uriHandler: UriHandler,
    library: Library
) {
    val url = library.licenses.firstOrNull()?.url?.takeIf { it.isNotBlank() }
        ?: library.website?.takeIf { it.isNotBlank() }
        ?: return
    runCatching { uriHandler.openUri(url) }
}

/** One M3 Expressive segmented row for a generated library entry. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LicenseRow(
    library: Library,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val licenseName = library.licenses.firstOrNull()?.name
    val version = library.artifactVersion?.takeIf { it.isNotBlank() }?.let { "v$it" }
    val metaLine = listOfNotNull(licenseName, version).joinToString(" · ")
    val author = library.developers.firstOrNull()?.name?.takeIf { it.isNotBlank() }
        ?: library.organization?.name?.takeIf { it.isNotBlank() }
    val description = library.description?.takeIf { it.isNotBlank() }

    SegmentedListItem(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shapes = ListItemDefaults.segmentedShapes(
            index = index,
            count = count,
        ),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        leadingContent = {
            LibraryBadge(name = library.name)
        },
        content = {
            Text(
                text = library.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Column {
                if (metaLine.isNotBlank()) {
                    Text(
                        text = metaLine,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                val detailLine = listOfNotNull(author, description).joinToString(" — ")
                if (detailLine.isNotBlank()) {
                    Text(
                        text = detailLine,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
    )
}

/** Tonal initial badge, echoing the contributor avatars on the About page. */
@Composable
private fun LibraryBadge(
    name: String,
    modifier: Modifier = Modifier
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

/** M3 Expressive summary header shown above the generated list. */
@Composable
private fun LicensesHeader(
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MoneySpacing.md,
                    vertical = MoneySpacing.md,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.width(MoneySpacing.sm))
            Column(verticalArrangement = Arrangement.spacedBy(MoneySpacing.micro)) {
                Text(
                    text = "$count open source libraries",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Generated at build time from the app's dependencies",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun LicensesLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(MoneySpacing.sm))
            Text(
                text = "Loading licenses…",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LicensesError(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    LicensesMessage(
        modifier = modifier,
        title = "Couldn't load licenses",
        body = "The generated license data is missing. " +
                "Rebuild the app so the AboutLibraries plugin can regenerate it, then try again.",
        actionLabel = "Retry",
        onAction = onRetry,
    )
}

@Composable
private fun LicensesEmpty(modifier: Modifier = Modifier) {
    LicensesMessage(
        modifier = modifier,
        title = "No libraries found",
        body = "The build-time scan reported no open source dependencies.",
        actionLabel = null,
        onAction = null,
    )
}

@Composable
private fun LicensesMessage(
    title: String,
    body: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = MoneySpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MoneySpacing.sm),
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(MoneySpacing.xs))
                FilledTonalButton(onClick = onAction) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LicensesHeaderPreview() {
    WhereIsMyMoneyTheme {
        LicensesHeader(count = 42, modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LicenseRowPreview() {
    val sample = Library(
        uniqueId = "org.jetbrains.kotlinx:kotlinx-serialization-json",
        artifactVersion = "1.11.0",
        name = "kotlinx-serialization-json",
        description = "Kotlin multiplatform serialization runtime library",
        website = "https://github.com/Kotlin/kotlinx.serialization",
        developers = emptyList(),
        organization = null,
        scm = null,
        licenses = setOf(
            com.mikepenz.aboutlibraries.entity.License(
                name = "Apache-2.0",
                url = "https://www.apache.org/licenses/LICENSE-2.0",
                hash = "apache-2.0",
            )
        ),
    )
    WhereIsMyMoneyTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            LicenseRow(library = sample, index = 0, count = 2, onClick = {})
            LicenseRow(library = sample, index = 1, count = 2, onClick = {})
        }
    }
}
