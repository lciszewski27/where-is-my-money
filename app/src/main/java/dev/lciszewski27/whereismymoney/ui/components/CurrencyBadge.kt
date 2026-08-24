package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo

/**
 * Small badge showing a 3-letter currency code.
 */
@Composable
fun CurrencyBadge(
    currencyCode: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val info = CurrencyInfo.fromCode(currencyCode)
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 0.dp
    ) {
        Text(
            text = info.symbol,
            modifier = Modifier,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = (size.value / 2.5).sp
        )
    }
}