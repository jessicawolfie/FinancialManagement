package com.example.financialmanagement.ui.transactions

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialmanagement.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel,
    transactionId: Long
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transactionId) {
        if (transactionId == -1L) {
            viewModel.clear()
        } else {
            viewModel.loadTransaction(transactionId)
        }
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing) "Edit Transaction" else "New Transaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.delete() }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Vermelho
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Branco)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CinzaFundo)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Branco)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeButton(
                        modifier = Modifier.weight(1f),
                        text = "↑ Income",
                        selected = uiState.type == "INCOME",
                        activeColor = Verde,
                        onClick = { viewModel.onTypeChange("INCOME") }
                    )

                    TypeButton(
                        modifier = Modifier.weight(1f),
                        text = "↓ Expense",
                        selected = uiState.type == "EXPENSE",
                        activeColor = Vermelho,
                        onClick = { viewModel.onTypeChange("EXPENSE") }
                    )
                }
            }
            
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Branco)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        label = "Transaction Name",
                        placeholder = "e.g. Market, Salary...",
                        value = uiState.description,
                        onValueChange = { viewModel.onDescriptionChange(it) }
                    )

                    InputField(
                        label = "Amount",
                        placeholder = "0.00",
                        value = uiState.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        keyboardType = KeyboardType.Decimal
                    )

                    DropdownField(
                        label = "Category",
                        placeholder = "Select a category",
                        options = categories,
                        selectedOption = uiState.selectedCategory,
                        optionText = { it.name },
                        onOptionSelected = { viewModel.onCategoryChange(it) }
                    )

                    DropdownField(
                        label = "Account",
                        placeholder = "Select an account",
                        options = accounts,
                        selectedOption = uiState.selectedAccount,
                        optionText = { it.name },
                        onOptionSelected = { viewModel.onAccountChange(it) }
                    )

                    DateField(
                        date = uiState.date,
                        onDateSelected = { viewModel.onDataChange(it) },
                        context = context
                    )

                    InputField(
                        label = "Note (optional)",
                        placeholder = "Add details...",
                        value = uiState.note,
                        onValueChange = { viewModel.onNoteChange(it) },
                        lines = 3
                    )
                }
            }

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (uiState.type == "INCOME") Verde else Vermelho)
            ) {
                Text(
                    text = if (uiState.isEditing) "Save Changes" else "Add Transaction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TypeButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val bgColor = if (selected) activeColor else CinzaFundo
    val textColor = if (selected) Branco else CinzaTexto

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun InputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    lines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CinzaTexto,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = lines,
            maxLines = lines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Verde,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun <T> DropdownField(
    label: String,
    placeholder: String,
    options: List<T>,
    selectedOption: T?,
    optionText: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CinzaTexto,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (expanded) Verde else CinzaTexto.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption?.let { optionText(it) } ?: placeholder,
                    color = if (selectedOption != null) Preto else CinzaTexto,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = CinzaTexto
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionText(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateField(
    date: Date,
    onDateSelected: (Date) -> Unit,
    context: android.content.Context
) {
    val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    val calendar = Calendar.getInstance().apply { time = date }

    Column {
        Text(
            text = "Date",
            fontSize = 12.sp,
            color = CinzaTexto,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CinzaTexto.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val cal = Calendar.getInstance()
                            cal.set(year, month, day)
                            onDateSelected(cal.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = format.format(date),
                    color = Preto,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Select date",
                    tint = CinzaTexto
                )
            }
        }
    }
}
