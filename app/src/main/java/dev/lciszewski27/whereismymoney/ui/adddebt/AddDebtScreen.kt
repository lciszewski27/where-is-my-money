package dev.lciszewski27.whereismymoney.ui.adddebt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.util.MoneyInput
import dev.lciszewski27.whereismymoney.domain.util.SplitCalculator
import dev.lciszewski27.whereismymoney.ui.components.PersonAvatar
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddDebtSheetContent(
    uiState: AddDebtUiState,
    onEvent: (AddDebtUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MoneySpacing.xl)
            .padding(bottom = MoneySpacing.xxl)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MoneySpacing.md)
    ) {
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (uiState.isEditing) "Edit Debt" else "New Debt",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onEvent(AddDebtUiEvent.Dismiss) }) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        }

        // ── Debt Type Segmented Button ──────────────────────────────
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.debtType == DebtType.THEY_OWE_ME,
                onClick = { onEvent(AddDebtUiEvent.DebtTypeChanged(DebtType.THEY_OWE_ME)) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = {
                    Icon(
                        Icons.Outlined.ArrowDownward, contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            ) { Text("They Owe Me", style = MaterialTheme.typography.labelLarge) }
            SegmentedButton(
                selected = uiState.debtType == DebtType.I_OWE_THEM,
                onClick = { onEvent(AddDebtUiEvent.DebtTypeChanged(DebtType.I_OWE_THEM)) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = {
                    Icon(
                        Icons.Outlined.ArrowUpward, contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            ) { Text("I Owe Them", style = MaterialTheme.typography.labelLarge) }
        }

        // ── Amount Input + Currency Picker ─────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
        ) {
            OutlinedTextField(
                value = uiState.amountText,
                onValueChange = { onEvent(AddDebtUiEvent.AmountChanged(it)) },
                label = { Text("Amount") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.AttachMoney, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1.5f),
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            CurrencyDropdown(
                selectedCurrency = uiState.currency,
                onCurrencySelected = { onEvent(AddDebtUiEvent.CurrencyChanged(it)) },
                modifier = Modifier.weight(1f)
            )
        }

        // ── Contact Selector ──────────────────────────────────────────
        if (uiState.splitMode) {
            SplitPersonSelector(
                uiState = uiState,
                onEvent = onEvent
            )
        } else if (uiState.selectedPersonId != null) {
            val person = uiState.persons.firstOrNull { it.id == uiState.selectedPersonId }
            if (person != null) {
                Text(
                    text = "Person: ${person.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Text(
                text = "Who is involved?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(MoneySpacing.xs)) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = if (uiState.showNewPersonField) uiState.newPersonName
                        else uiState.selectedPersonName,
                        onValueChange = {
                            if (uiState.showNewPersonField) {
                                onEvent(AddDebtUiEvent.NewPersonNameChanged(it))
                            }
                        },
                        placeholder = { Text("Search or type new name...") },
                        readOnly = !uiState.showNewPersonField,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                onEvent(AddDebtUiEvent.ToggleNewPersonField)
                            },
                            leadingIcon = { Icon(Icons.Filled.PersonAdd, null) },
                            text = { Text("Create new person") }
                        )
                        if (uiState.persons.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = MoneySpacing.xxs))
                            uiState.persons.forEach { person ->
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        onEvent(AddDebtUiEvent.SelectPerson(person.id, person.name))
                                    },
                                    leadingIcon = {
                                        PersonAvatar(person.name, person.colorSeed, size = 32.dp)
                                    },
                                    text = { Text(person.name) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Group split toggle (new debts only) ────────────────────
        if (!uiState.isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Split between people",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Divide the total equally across everyone selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.splitMode,
                    onCheckedChange = { onEvent(AddDebtUiEvent.ToggleSplitMode) }
                )
            }
        }

        // ── Description ────────────────────────────────────────────
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { onEvent(AddDebtUiEvent.DescriptionChanged(it)) },
            label = { Text("Description (optional)") },
            leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        // ── Due Date ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateText = uiState.dueDateMillis?.let {
                SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(Date(it))
            } ?: "No due date"

            Text(
                text = "Due: $dateText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FilledTonalButton(
                onClick = { onEvent(AddDebtUiEvent.ToggleDatePicker) },
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.Filled.CalendarMonth, contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(MoneySpacing.xxs))
                Text(if (uiState.dueDateMillis != null) "Change" else "Set Date")
            }
        }

        // Date picker dialog
        if (uiState.showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.dueDateMillis
            )
            DatePickerDialog(
                onDismissRequest = { onEvent(AddDebtUiEvent.ToggleDatePicker) },
                confirmButton = {
                    TextButton(onClick = {
                        onEvent(AddDebtUiEvent.SetDueDate(datePickerState.selectedDateMillis))
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        onEvent(AddDebtUiEvent.SetDueDate(null))
                    }) { Text("Clear") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        // ── Category ───────────────────────────────────────────────
        var categoryExpanded by remember { mutableStateOf(false) }
        val selectedCategory =
            uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
        ) {
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "No category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category (optional)") },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Folder, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            categoryExpanded = false
                            onEvent(AddDebtUiEvent.SelectCategory(null))
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = {
                            Text(
                                "No category",
                                fontWeight = if (uiState.selectedCategoryId == null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    uiState.categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                categoryExpanded = false
                                onEvent(AddDebtUiEvent.SelectCategory(category.id))
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Folder, contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            text = {
                                Text(
                                    category.name,
                                    fontWeight = if (category.id == uiState.selectedCategoryId) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
            if (uiState.selectedCategoryId != null) {
                IconButton(onClick = { onEvent(AddDebtUiEvent.SelectCategory(null)) }) {
                    Icon(
                        Icons.Filled.Close, contentDescription = "Clear category",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(Modifier.height(MoneySpacing.xs))

        // ── Save Button (M3 Expressive: morphs shape on press) ───
        val canSave = uiState.amountCents > 0 && if (uiState.splitMode) {
            uiState.splitPersonIds.isNotEmpty()
        } else {
            uiState.selectedPersonId != null || uiState.newPersonName.isNotBlank()
        }
        Button(
            onClick = { onEvent(AddDebtUiEvent.SaveDebt) },
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = canSave
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(MoneySpacing.xs))
            Text(
                text = when {
                    uiState.isEditing -> "Update Debt"
                    uiState.splitMode -> "Split between ${uiState.splitPersonIds.size}"
                    else -> "Save Debt"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Multi-select person list for group expense splitting.
 * Each row is a checkbox row showing the person's equal share preview.
 * Quiet M3 list (no nested cards): selection is carried by the checkbox
 * state plus a tonal share label, per the 1:3 containment budget.
 */
@Composable
private fun SplitPersonSelector(
    uiState: AddDebtUiState,
    onEvent: (AddDebtUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MoneySpacing.xxs)
    ) {
        Text(
            text = "Who shares this expense?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (uiState.persons.isEmpty()) {
            Text(
                text = "No people yet — close this sheet and add someone first.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val shares = SplitCalculator.equalShares(uiState.amountCents, uiState.splitPersonIds.size)
            val orderedIds = uiState.persons
                .filter { it.id in uiState.splitPersonIds }
                .map { it.id }
            val shareById = orderedIds.zip(shares).toMap()
            val currencySymbol = CurrencyInfo.fromCode(uiState.currency).symbol

            uiState.persons.forEach { person ->
                val checked = person.id in uiState.splitPersonIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            role = Role.Checkbox,
                            onClick = { onEvent(AddDebtUiEvent.ToggleSplitPerson(person.id)) }
                        )
                        .padding(horizontal = MoneySpacing.xs, vertical = MoneySpacing.xxs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { onEvent(AddDebtUiEvent.ToggleSplitPerson(person.id)) }
                    )
                    PersonAvatar(person.name, person.colorSeed, size = 32.dp)
                    Text(
                        text = person.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (checked) {
                        val share = shareById[person.id] ?: 0L
                        Text(
                            text = "${MoneyInput.formatCentsForInput(share)} $currencySymbol",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val info = CurrencyInfo.fromCode(selectedCurrency)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${info.symbol} ${info.code}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Currency") },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CurrencyInfo.AVAILABLE.forEach { currency ->
                DropdownMenuItem(
                    onClick = { expanded = false; onCurrencySelected(currency.code) },
                    text = { Text("${currency.symbol}  ${currency.code} - ${currency.name}") }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddDebtSheetPreview() {
    WhereIsMyMoneyTheme {
        AddDebtSheetContent(
            uiState = AddDebtUiState(
                persons = listOf(
                    Person("1", "Alice Johnson", 123, 0),
                    Person("2", "Bob Smith", 456, 0)
                ),
                amountText = "25.50",
                currency = "USD",
                debtType = DebtType.THEY_OWE_ME
            ),
            onEvent = {}
        )
    }
}