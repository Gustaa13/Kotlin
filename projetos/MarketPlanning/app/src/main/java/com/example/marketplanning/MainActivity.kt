package com.example.marketplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.marketplanning.ui.theme.MarketPlanningTheme
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketPlanningTheme {
                ShoppingListScreen()
            }
        }
    }
}

@Composable
fun RuleOfThreeCalculator(
    valueA: String,
    onValueAChange: (String) -> Unit,
    valueB: String,
    onValueBChange: (String) -> Unit,
    valueC: String,
    onValueCChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val resultX = remember(valueA, valueB, valueC) {
        val a = valueA.replace(',', '.').toDoubleOrNull()
        val b = valueB.replace(',', '.').toDoubleOrNull()
        val c = valueC.replace(',', '.').toDoubleOrNull()

        // Cálculo da regra de 3: X = (B * C) / A
        if (a != null && b != null && c != null && a != 0.0) {
            "%.2f".format((b * c) / a).replace('.', ',')
        } else {
            "X"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Regra de Três") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "A / B = C / X",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Grid 2x2 para Inputs/Resultado
                Column(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Linha 1: A e B (Input)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RuleOfThreeInput(
                            value = valueA,
                            onValueChange = onValueAChange,
                            label = "A",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        RuleOfThreeInput(
                            value = valueB,
                            onValueChange = onValueBChange,
                            label = "B",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Linha 2: C e X (Input e Resultado)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RuleOfThreeInput(
                            value = valueC,
                            onValueChange = onValueCChange,
                            label = "C",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        // Quadrado do Resultado (X)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                // --- ALTURA FIXA ADICIONADA ---
                                .height(75.dp)
                                // ------------------------------
                                .border(1.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = resultX,
                                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("FECHAR")
            }
        }
    )
}

@Composable
fun RuleOfThreeInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { text ->
            onValueChange(text.filter { char -> char.isDigit() || char == '.' || char == ',' })
        },
        label = { Text(label, maxLines = 1) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        // --- ALTURA FIXA ADICIONADA ---
        modifier = modifier.fillMaxWidth().height(75.dp),
        // ------------------------------
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen() {
    var items by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showRuleOfThreePopup by remember { mutableStateOf(false) }

    var ruleOfThreeValueA by remember { mutableStateOf("") }
    var ruleOfThreeValueB by remember { mutableStateOf("") }
    var ruleOfThreeValueC by remember { mutableStateOf("") }

    var newName by remember { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }

    val totalQuantity = items.sumOf { it.quantity }
    val totalPrice = items.sumOf { it.quantity * it.price }

    val focusRequesterProduct = remember { FocusRequester() }
    val focusRequesterQuantity = remember { FocusRequester() }
    val focusRequesterPrice = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun handleEdit(oldItem: ShoppingItem, newName: String, newQuantity: Int, newPrice: Double) {
        items = items.map { item ->
            if (item == oldItem) {
                item.copy(name = newName, quantity = newQuantity, price = newPrice)
            } else {
                item
            }
        }
    }

    @Composable
    fun ItemInputRow() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Produto") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .focusRequester(focusRequesterProduct),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterQuantity.requestFocus() }
                )
            )

            OutlinedTextField(
                value = newQuantity,
                onValueChange = { newQuantity = it.filter { char -> char.isDigit() } },
                label = { Text("Qtd") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterPrice.requestFocus() }
                ),
                modifier = Modifier
                    .width(80.dp)
                    .padding(end = 8.dp)
                    .focusRequester(focusRequesterQuantity)
            )

            OutlinedTextField(
                value = newPrice,
                onValueChange = {
                    newPrice = it.filter { char -> char.isDigit() || char == '.' || char == ',' }
                },
                label = { Text("Preço") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val cleanPrice = newPrice.replace(',', '.')
                        val q = newQuantity.toIntOrNull() ?: 0
                        val p = cleanPrice.toDoubleOrNull() ?: 0.0
                        if (newName.isNotBlank() && q > 0) {
                            items = items + ShoppingItem(newName, q, p)
                            newName = ""
                            newQuantity = ""
                            newPrice = ""
                            focusRequesterProduct.requestFocus()
                        } else {
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier
                    .width(100.dp)
                    .padding(end = 8.dp)
                    .focusRequester(focusRequesterPrice)
            )
        }
    }

    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lista de Compras") },
                actions = {
//                    IconButton(onClick = { /* TODO: Implementar lógica de Importar */ }) {
//                        Icon(
//                            imageVector = Icons.Filled.KeyboardArrowDown, // Ícone para Importar
//                            contentDescription = "Importar Lista"
//                        )
//                    }
//                    // 2. ÍCONE DE EXPORTAR (FileDownload)
//                    IconButton(onClick = { /* TODO: Implementar lógica de Exportar */ }) {
//                        Icon(
//                            imageVector = Icons.Filled.KeyboardArrowUp, // Ícone para Exportar
//                            contentDescription = "Exportar Lista"
//                        )
//                    }
                    // 3. ÍCONE DA REGRA DE TRÊS (Mantido, mas usei o Icon.Menu)
                    IconButton(onClick = { showRuleOfThreePopup = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu, // Ícone para Regra de Três
                            contentDescription = "Regra de Três"
                        )
                    }
                },
                modifier = Modifier.padding(0.dp, if(isKeyboardOpen) 250.dp else 50.dp, 0.dp, 0.dp)
            )

        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                ItemInputRow()
                Spacer(modifier = Modifier.height(80.dp))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(items) { item ->
                    ShoppingListItem(
                        item = item,
                        onCheckedChange = { checked ->
                            items = items.map {
                                if (it == item) it.copy(bought = checked) else it
                            }
                        },
                        onDelete = {
                            items = items - item
                        },
                        onEdit = ::handleEdit
                    )
                    HorizontalDivider()
                }

                if (items.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .padding(end = 8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "Quantidade total: $totalQuantity",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Valor total: R$ %.2f".format(totalPrice),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showRuleOfThreePopup) {
        RuleOfThreeCalculator(
            valueA = ruleOfThreeValueA,
            onValueAChange = { ruleOfThreeValueA = it },
            valueB = ruleOfThreeValueB,
            onValueBChange = { ruleOfThreeValueB = it },
            valueC = ruleOfThreeValueC,
            onValueCChange = { ruleOfThreeValueC = it },
            onDismiss = { showRuleOfThreePopup = false }
        )
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (ShoppingItem, String, Int, Double) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var editedPrice by remember { mutableStateOf("%.2f".format(item.price).replace('.', ',')) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            // --- EDITING VIEW ---
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Name Input
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Produto") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quantity Input
                    OutlinedTextField(
                        value = editedQuantity,
                        onValueChange = { editedQuantity = it.filter { char -> char.isDigit() } },
                        label = { Text("Qtd") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 4.dp),
                        singleLine = true
                    )
                    // Price Input
                    OutlinedTextField(
                        value = editedPrice,
                        onValueChange = {
                            editedPrice = it.filter { char -> char.isDigit() || char == '.' || char == ',' }
                        },
                        label = { Text("Preço") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(100.dp),
                        singleLine = true
                    )
                }

                // Save and Cancel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isEditing = false }) {
                        Text("CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val cleanPrice = editedPrice.replace(',', '.')
                            val q = editedQuantity.toIntOrNull() ?: 0
                            val p = cleanPrice.toDoubleOrNull() ?: 0.0

                            if (editedName.isNotBlank() && q > 0) {
                                onEdit(item, editedName.trim(), q, p)
                                isEditing = false
                            }
                        }
                    ) {
                        Text("SALVAR")
                    }
                }
            }

        } else {
            // --- DISPLAY VIEW ---
            Checkbox(
                checked = item.bought,
                onCheckedChange = onCheckedChange
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = if (item.bought) MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ) else MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${item.quantity} x R$%.2f = R$%.2f".format(item.price, item.quantity * item.price),
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = if (item.bought) TextDecoration.LineThrough else null,
                        color = if (item.bought) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                )
            }

            // Edit Button
            IconButton(onClick = {
                editedName = item.name
                editedQuantity = item.quantity.toString()
                editedPrice = "%.2f".format(item.price).replace('.', ',')
                isEditing = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar"
                )
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    MarketPlanningTheme {
        ShoppingListScreen()
    }
}
