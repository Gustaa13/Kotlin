package com.example.hub.screens.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hub.R
import com.example.hub.utils.LogHelper
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.text.replace

class CalculatorActivity : AppCompatActivity() {
    private lateinit var tvDisplay: TextView
    private lateinit var PreviousCalculationDisplay: TextView

    private var prevInput: String = ""
    private var currentInput: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var justCalculated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        LogHelper.i("CalculatorActivity iniciada")

        // TextView de display
        tvDisplay = findViewById(R.id.txtResultado)

        PreviousCalculationDisplay = findViewById(R.id.txtContaAnterior)

        // Botões de dígitos
        val digits = listOf(
            "0" to R.id.btn0,
            "1" to R.id.btn1,
            "2" to R.id.btn2,
            "3" to R.id.btn3,
            "4" to R.id.btn4,
            "5" to R.id.btn5,
            "6" to R.id.btn6,
            "7" to R.id.btn7,
            "8" to R.id.btn8,
            "9" to R.id.btn9,
            "," to R.id.btnPonto
        )
        digits.forEach { (digit, id) ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        // Botões de operações
        val ops = listOf(
            "+" to R.id.btnSomar,
            "-" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar,
            "÷" to R.id.btnDividir,
            "(" to R.id.btnInicioParenteses,
            ")" to R.id.btnFimParenteses
        )
        ops.forEach { (op, id) ->
            findViewById<Button>(id).setOnClickListener { onOperator(op) }
        }

        // Botão igual
        findViewById<Button>(R.id.btnIgual).setOnClickListener { onEquals() }

        // Botão limpar tudo
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }

        // Botão backspace
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }

        updateDisplay()
    }

    private fun appendDigit(d: String) {
        LogHelper.v("appendDigit chamado com valor: $d")
        if (justCalculated) {
            LogHelper.d("Novo cálculo iniciado após resultado anterior")
            justCalculated = false
        }
        currentInput += d
        updateDisplay()
    }

    private fun onOperator(op: String) {
        LogHelper.v("onOperator chamado com operador: $op")
        appendDigit(op)
    }

    private fun onEquals() {
        LogHelper.i("Usuário pressionou '='. Expressão: $currentInput")
        try {
            prevInput = currentInput
            currentInput = currentInput
                .replace("×", "*")
                .replace("÷", "/")
                .replace(",", ".")

            currentInput = SimpleMathEvaluator.eval(currentInput)
                .toString()
                .replace(".", ",")

            LogHelper.i("Resultado calculado com sucesso: $currentInput")
        } catch (e: Exception) {
            LogHelper.e("Erro ao calcular expressão: ${e.message}", e)
        }
        updateDisplay()
    }

    private fun clearAll() {
        LogHelper.i("Limpando todos os campos da calculadora")
        currentInput = ""
        operand = null
        pendingOp = null
        updateDisplay()
    }

    private fun backspace() {
        LogHelper.v("Backspace pressionado")
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        LogHelper.v("Atualizando display: $currentInput")
        PreviousCalculationDisplay.text = prevInput
        tvDisplay.text = when {
            currentInput.isNotEmpty() && !justCalculated -> formatNumberInput(currentInput)
            currentInput.isNotEmpty() && justCalculated -> currentInput
            currentInput.isEmpty() -> ""
            operand != null -> formatNumberInput(operand.toString())
            else -> ""
        }
    }

    private fun formatNumberInput(input: String): String {
        val normalized = input.replace(",", ".")

        val value = normalized.toDoubleOrNull() ?: return input

        val symbols = DecimalFormatSymbols(Locale.forLanguageTag("pt-BR")).apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }

        val formatter = DecimalFormat("#,###.######", symbols)
        return formatter.format(value)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        LogHelper.d("Salvando estado da calculadora")
        super.onSaveInstanceState(outState)
        outState.putString("currentInput", currentInput)
        outState.putDouble("operand", operand ?: Double.NaN)
        outState.putString("pendingOp", pendingOp)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        LogHelper.d("Restaurando estado da calculadora")
        currentInput = savedInstanceState.getString("currentInput", "")
        val opnd = savedInstanceState.getDouble("operand", Double.NaN)
        operand = if (opnd.isNaN()) null else opnd
        pendingOp = savedInstanceState.getString("pendingOp")
        updateDisplay()
    }
}