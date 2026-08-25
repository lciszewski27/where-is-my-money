package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.DebtItemWithPerson
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import androidx.compose.ui.platform.LocalLocale

private val PEEK_HEIGHT = 120.dp
private val EXPANDED_HEIGHT = 520.dp
private const val SNAP_THRESHOLD_PX = 100f

@Composable
fun ExpandableBottomDrawer(
    summary: DashboardSummary,
    upcomingRepayments: List<DebtItemWithPerson>,
    recentActivity: List<DebtItemWithPerson>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val displayCurrency = CurrencyInfo.fromCode(summary.primaryCurrency).symbol
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }

    val peekHeightPx = with(density) { PEEK_HEIGHT.toPx() }
    val expandedHeightPx = with(density) { EXPANDED_HEIGHT.toPx() }
    val collapsedTranslationPx = expandedHeightPx - peekHeightPx

    val targetTranslationPx = if (isExpanded) 0f else collapsedTranslationPx

    val translationY by animateFloatAsState(
        targetValue = targetTranslationPx + dragOffsetPx,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f),
        label = "drawerTranslation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(EXPANDED_HEIGHT)
            .graphicsLayer {
                this.translationY = translationY.coerceIn(0f, collapsedTranslationPx)
            }
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { dragOffsetPx = 0f },
                    onVerticalDrag = { _, amount -> dragOffsetPx += amount },
                    onDragEnd = {
                        if (dragOffsetPx < -SNAP_THRESHOLD_PX) onExpandedChange(true)
                        else if (dragOffsetPx > SNAP_THRESHOLD_PX) onExpandedChange(false)
                        dragOffsetPx = 0f
                    },
                    onDragCancel = { dragOffsetPx = 0f }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header and Drag Handle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PEEK_HEIGHT)
                    .clickable { onExpandedChange(!isExpanded) }
                    .padding(bottom = MoneySpacing.xs)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        Modifier.width(32.dp).height(4.dp).clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                }

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = MoneySpacing.lg),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    DrawerMetric("They Owe Me", summary.totalReceivablesCents, displayCurrency, true, Modifier.weight(1f))
                    Spacer(Modifier.width(MoneySpacing.sm))
                    DrawerMetric("I Owe Them", summary.totalPayablesCents, displayCurrency, false, Modifier.weight(1f))
                    Spacer(Modifier.width(MoneySpacing.sm))
                    DrawerMetric("Net", summary.netBalanceCents, displayCurrency, summary.netBalanceCents >= 0, Modifier.weight(1f))
                }

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = MoneySpacing.lg, vertical = MoneySpacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${summary.totalActiveDebts} active debt${if (summary.totalActiveDebts != 1) "s" else ""} \u2022 ${summary.activeCurrencies.size} currency${if (summary.activeCurrencies.size != 1) "ies" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (isExpanded) "Less" else "More",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                            contentDescription = null,
                            Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = MoneySpacing.lg)
            ) {
                item { SectionHeader(Icons.Outlined.CalendarMonth, "Upcoming Repayments", upcomingRepayments.size) }
                if (upcomingRepayments.isEmpty()) {
                    item { EmptySection("No upcoming repayments", Icons.Outlined.Schedule) }
                } else {
                    items(upcomingRepayments, key = { "upcoming_${it.id}" }) { RepaymentRow(it) { onPersonClick(it.personId) } }
                }

                item {
                    HorizontalDivider(
                        Modifier.padding(horizontal = MoneySpacing.md, vertical = MoneySpacing.sm),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                item { SectionHeader(Icons.Filled.History, "Activity History", recentActivity.size) }
                if (recentActivity.isEmpty()) {
                    item { EmptySection("No activity yet", Icons.Filled.History) }
                } else {
                    items(recentActivity, key = { "recent_${it.id}" }) { ActivityRow(it) { onPersonClick(it.personId) } }
                }
            }
        }
    }
}

@Composable
private fun DrawerMetric(label: String, amountCents: Long, symbol: String, isPositive: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        val c = when { amountCents == 0L -> MaterialTheme.colorScheme.onSurface; isPositive -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.error }
        val s = if (amountCents < 0) "-" else ""; val a = abs(amountCents)
        Text("$s${a / 100}.${(a % 100).toString().padStart(2, '0')}$symbol", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = c, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String, count: Int) {
    Row(Modifier.fillMaxWidth().padding(horizontal = MoneySpacing.md, vertical = MoneySpacing.sm), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(MoneySpacing.xs))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        if (count > 0) Box(Modifier.clip(MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = MoneySpacing.xs, vertical = 2.dp)) {
            Text("$count", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun EmptySection(text: String, icon: ImageVector) {
    Row(Modifier.fillMaxWidth().padding(horizontal = MoneySpacing.md, vertical = MoneySpacing.md), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        Spacer(Modifier.width(MoneySpacing.xs))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
    }
}

@Composable
private fun RepaymentRow(item: DebtItemWithPerson, onClick: () -> Unit) {
    val d = item.debt; val sign = if (d.type == DebtType.THEY_OWE_ME) "+" else "-"
    val overdue = d.dueDateMillis != null && d.dueDateMillis < System.currentTimeMillis()
    val cur = CurrencyInfo.fromCode(d.currency).symbol
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = MoneySpacing.md, vertical = MoneySpacing.sm), verticalAlignment = Alignment.CenterVertically) {
        val dl = d.dueDateMillis ?: 0L
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(52.dp)) {
            Text(SimpleDateFormat("MMM", LocalLocale.current.platformLocale).format(Date(dl)).uppercase(), style = MaterialTheme.typography.labelSmall, color = if (overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(SimpleDateFormat("dd", LocalLocale.current.platformLocale).format(Date(dl)), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = if (overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            if (overdue) Text("OVERDUE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(MoneySpacing.sm))
        Column(Modifier.weight(1f)) {
            Text(item.personName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (d.description.isNotBlank()) Text(d.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            val ac = if (d.type == DebtType.THEY_OWE_ME) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            Text("$sign${d.amountCents / 100}.${(d.amountCents % 100).toString().padStart(2, '0')}$cur", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = ac)
            Text(if (d.type == DebtType.THEY_OWE_ME) "Owes you" else "You owe", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ActivityRow(item: DebtItemWithPerson, onClick: () -> Unit) {
    val d = item.debt; val cur = CurrencyInfo.fromCode(d.currency).symbol
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = MoneySpacing.md, vertical = MoneySpacing.sm), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(MaterialTheme.shapes.medium).background(if (d.isSettled) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            Icon(if (d.isSettled) Icons.AutoMirrored.Filled.FactCheck else Icons.Filled.KeyboardArrowUp, null, Modifier.size(20.dp), tint = if (d.isSettled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(MoneySpacing.sm))
        Column(Modifier.weight(1f)) {
            val desc = if (d.description.isNotBlank()) d.description else if (d.type == DebtType.THEY_OWE_ME) "${item.personName} owes you" else "You owe ${item.personName}"
            Text(desc, style = MaterialTheme.typography.bodyMedium, fontWeight = if (d.isSettled) FontWeight.Normal else FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${item.personName} \u2022 ${formatDate(d.timestamp)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(MoneySpacing.sm))
        Column(horizontalAlignment = Alignment.End) {
            val s = if (d.type == DebtType.THEY_OWE_ME) "+" else "-"
            val ac = when { d.isSettled -> MaterialTheme.colorScheme.outline; d.type == DebtType.THEY_OWE_ME -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.error }
            Text("$s${d.amountCents / 100}.${(d.amountCents % 100).toString().padStart(2, '0')}$cur", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ac)
            if (d.isSettled) Text("Settled", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatDate(epoch: Long): String = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(epoch))
