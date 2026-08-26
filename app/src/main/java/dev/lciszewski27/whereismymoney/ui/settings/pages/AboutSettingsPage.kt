package dev.lciszewski27.whereismymoney.ui.settings.pages

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import dev.lciszewski27.whereismymoney.BuildConfig
import dev.lciszewski27.whereismymoney.domain.model.Contributor
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun AboutSettingsPage(onEvent: (SettingsUiEvent) -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var contributors by remember { mutableStateOf<List<Contributor>>(emptyList()) }

    LaunchedEffect(Unit) {
        contributors = loadContributors(context.resources)
    }

    AboutSettingsPageContent(
        contributors = contributors,
        onContributorClick = { contributor ->
            if (contributor.githubUrl.isNotBlank()) {
                uriHandler.openUri(contributor.githubUrl)
            }
        }
    )
}

@Composable
private fun AboutSettingsPageContent(
    contributors: List<Contributor>,
    onContributorClick: (Contributor) -> Unit
) {
    val versionName = BuildConfig.VERSION_NAME

    val context = LocalContext.current

    val appIconDrawable = remember(context) {
        context.packageManager.getApplicationIcon(context.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MoneySpacing.md),
        verticalArrangement = Arrangement.spacedBy(MoneySpacing.md)
    ) {
        // ── App Info ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MoneySpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                    Icon(
                        painter = rememberAsyncImagePainter(model = appIconDrawable),
                        contentDescription = "Ikona aplikacji",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(100.dp)
                    )
            }
            Text(
                text = "Where is my money?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Version $versionName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (contributors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.FavoriteBorder, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(MoneySpacing.sm))
                    Text(
                        "Loading contributors...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // ── Header ──────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start

            ) {
                Icon(
                    Icons.Filled.Favorite, contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(MoneySpacing.sm))
                Text(
                    text = "${contributors.size} people behind the project",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 36.sp
                )
            }

            // ── Contributor Cards ─────────────────────────────────
            val numberContributors = contributors.size
            val isPreview = LocalInspectionMode.current
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                contributors.forEachIndexed { index, contributor ->
                    var visible by remember { mutableStateOf(isPreview) }
                    LaunchedEffect(Unit) {
                        if (!isPreview) {
                            kotlinx.coroutines.delay((index * 50).milliseconds)
                            visible = true
                        }
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) +
                                slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                    ) {
                        ContributorCard(
                            contributor = contributor,
                            index = index,
                            count = numberContributors,
                            onClick = { onContributorClick(contributor) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(MoneySpacing.xl))
        }
    }
}

private val avatarBackgrounds = listOf(
    0xFF1A6B52, 0xFF4A6FA5, 0xFF7B52AB, 0xFFC43E00,
    0xFF00897B, 0xFF5C6BC0, 0xFFAD1457, 0xFF00695C,
    0xFF4527A0, 0xFF283593, 0xFF2E7D32, 0xFFE65100
)

private val contributorJson = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributorCard(
    contributor: Contributor,
    onClick: () -> Unit,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    SegmentedListItem(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shapes = ListItemDefaults.segmentedShapes(
            index = index,
            count = count
        ),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        content = {
            Text(
                text = contributor.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(
                text = contributor.role,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                ContributorAvatar(
                    name = contributor.name,
                    imageUrl = contributor.imageUrl,
                    size = 64.dp
                )
            }
        },
    )

}


@Composable
private fun ContributorAvatar(
    name: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    val bgColor =
        Color(avatarBackgrounds[kotlin.math.abs(name.hashCode()) % avatarBackgrounds.size])

    if (imageUrl.isNotBlank()) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(size / 2)
                            .padding(4.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                },
                error = {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.45).sp
                    )
                }
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.45).sp
            )
        }
    }
}

private fun loadContributors(resources: Resources): List<Contributor> {
    return try {
        val jsonString = resources.openRawResource(
            dev.lciszewski27.whereismymoney.R.raw.contributors
        ).bufferedReader().use { it.readText() }
        contributorJson.decodeFromString<List<Contributor>>(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutSettingsPagePreview() {
    val sampleContributors = listOf(
        Contributor(name = "Łukasz", role = "Lead Developer"),
        Contributor(name = "Android Dev", role = "Contributor"),
        Contributor(name = "Designer", role = "UI/UX")
    )
    WhereIsMyMoneyTheme {
        AboutSettingsPageContent(
            contributors = sampleContributors,
            onContributorClick = {}
        )
    }
}
