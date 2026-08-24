package dev.lciszewski27.whereismymoney.ui.adddebt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.ui.components.PersonAvatar
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
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
                    Icon(Icons.Outlined.ArrowDownward, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                }
            ) { Text("They Owe Me", style = MaterialTheme.typography.labelLarge) }
            SegmentedButton(
                selected = uiState.debtType == DebtType.I_OWE_THEM,
                onClick = { onEvent(AddDebtUiEvent.DebtTypeChanged(DebtType.I_OWE_THEM)) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = {
                    Icon(Icons.Outlined.ArrowUpward, contentDescription = null,
                        modifier = Modifier.size(18.dp))
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
                    Icon(Icons.Filled.AttachMoney, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
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
        if (uiState.selectedPersonId != null) {
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
                            .menuAnchor(MenuAnchorType.PrimaryEditable)
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
            FilledTonalButton(onClick = { onEvent(AddDebtUiEvent.ToggleDatePicker) },
                shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null,
                    modifier = Modifier.size(18.dp))
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

        Spacer(Modifier.height(MoneySpacing.xs))

        // ── Save Button ─────────────────────────────────────────────
        Button(
            onClick = { onEvent(AddDebtUiEvent.SaveDebt) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large,
            enabled = (uiState.selectedPersonId != null || uiState.newPersonName.isNotBlank()) &&
                    uiState.amountCents > 0
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(MoneySpacing.xs))
            Text(
                text = if (uiState.isEditing) "Update Debt" else "Save Debt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
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
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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