package dev.lciszewski27.whereismymoney.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.WhereIsMyMoneyApp
import dev.lciszewski27.whereismymoney.domain.model.StatsSummary
import dev.lciszewski27.whereismymoney.domain.model.StatsMonthlyTrend
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as WhereIsMyMoneyApp
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<StatsViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return StatsViewModel(
                    repository = app.repository,
                    preferences = app.preferences
                ) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StatsScreenContent(
                stats = uiState.stats,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun StatsScreenContent(
    stats: StatsSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MoneySpacing.md, vertical = MoneySpacing.lg),
        verticalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
    ) {
        // ── Section: Overview ────────────────────────────────────────
        Text(
            "Overview",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
        ) {
            StatCard(
                title = "Active Debts",
                value = "${stats.activeDebtCount}",
                subtitle = "${stats.settledDebtCount} settled",
                icon = Icons.Filled.AttachMoney,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Active",
                value = formatCents(stats.totalActiveCents, stats.primaryCurrency),
                subtitle = "All currencies",
                icon = Icons.Filled.TrendingUp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
        ) {
            StatCard(
                title = "Receivables",
                value = formatCents(stats.totalReceivablesCents, stats.primaryCurrency),
                subtitle = "They owe me",
                icon = Icons.Filled.TrendingUp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Payables",
                value = formatCents(stats.totalPayablesCents, stats.primaryCurrency),
                subtitle = "I owe them",
                icon = Icons.Filled.TrendingDown,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(MoneySpacing.xs))

        // ── Section: Debt Over Time ─────────────────────────────────
        Text(
            "Debt Over Time",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = RoundedCornerShape(MoneySpacing.md)
        ) {
            Column(modifier = Modifier.padding(MoneySpacing.md)) {
                if (stats.monthlyTrends.isEmpty()) {
                    Text(
                        "No debt history yet. Add some debts to see trends.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    DebtTrendChart(
                        trends = stats.monthlyTrends,
                        primaryCurrency = stats.primaryCurrency,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(Modifier.height(MoneySpacing.sm))
                    Text(
                        "Net balance over time (in ${stats.primaryCurrency})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(MoneySpacing.xs))

        // ── Section: Status Distribution ────────────────────────────
        Text(
            "Status Distribution",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = RoundedCornerShape(MoneySpacing.md)
        ) {
            Column(modifier = Modifier.padding(MoneySpacing.md)) {
                val total = (stats.activeDebtCount + stats.settledDebtCount).toFloat()
                if (total > 0) {
                    val activeRatio = stats.activeDebtCount / total
                    val settledRatio = stats.settledDebtCount / total
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val tertiaryColor = MaterialTheme.colorScheme.tertiary

                    // Active progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 2.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = primaryColor)
                            }
                        }
                        Spacer(Modifier.width(MoneySpacing.sm))
                        Text("Active", modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodyMedium)
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { activeRatio },
                            modifier = Modifier.weight(1f).height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(Modifier.width(MoneySpacing.sm))
                        Text("${stats.activeDebtCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(MoneySpacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 2.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = tertiaryColor)
                            }
                        }
                        Spacer(Modifier.width(MoneySpacing.sm))
                        Text("Settled", modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodyMedium)
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { settledRatio },
                            modifier = Modifier.weight(1f).height(8.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(Modifier.width(MoneySpacing.sm))
                        Text("${stats.settledDebtCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        "No debts yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(MoneySpacing.xl))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(MoneySpacing.md)
    ) {
        Column(
            modifier = Modifier.padding(MoneySpacing.md),
            verticalArrangement = Arrangement.spacedBy(MoneySpacing.xxs)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon, contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(MoneySpacing.xxs))
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DebtTrendChart(
    trends: List<StatsMonthlyTrend>,
    primaryCurrency: String,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40f
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        if (trends.size < 2) return@Canvas

        // Find min/max for scaling
        val values = trends.map { it.netCents.toFloat() }
        val minVal = values.min()
        val maxVal = values.max()
        val range = (maxVal - minVal).coerceAtLeast(1f)

        // Draw grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = padding + chartHeight * (1f - i.toFloat() / gridLines)
            drawLine(
                color = surfaceVariant,
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1f
            )
        }

        // Draw line path
        val stepX = chartWidth / (trends.size - 1).coerceAtLeast(1)
        val path = Path()
        trends.forEachIndexed { index, trend ->
            val x = padding + index * stepX
            val y = padding + chartHeight * (1f - (trend.netCents - minVal) / range)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw dots
        trends.forEachIndexed { index, trend ->
            val x = padding + index * stepX
            val y = padding + chartHeight * (1f - (trend.netCents - minVal) / range)
            val dotColor = if (trend.netCents >= 0) tertiary else errorColor
            drawCircle(color = dotColor, radius = 4f, center = Offset(x, y))
        }

        // Draw month labels on X axis
        val paint = android.graphics.Paint().apply {
            color = onSurface.copy(alpha = 0.6f).hashCode()
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val step = (trends.size / 4).coerceAtLeast(1)
        for (i in trends.indices step step) {
            val label = trends[i].yearMonth.substring(5) // "MM"
            val x = padding + i * stepX
            drawContext.canvas.nativeCanvas.drawText(
                label, x, height - 8f, paint
            )
        }
    }
}

private fun formatCents(cents: Long, currency: String): String {
    val symbol = dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo.fromCode(currency).symbol
    val major = cents / 100
    val minor = (cents % 100).toString().padStart(2, '0')
    return "$major.$minor$symbol"
}

// Preview is intentionally omitted as StatsScreen needs app context