package com.example.hub.screens.calculator
import com.example.hub.utils.LogHelper
import java.util.Stack

object SimpleMathEvaluator {
    fun eval(expression: String): Double {
        LogHelper.d("Iniciando avaliação da expressão: $expression")

        val output = mutableListOf<String>()
        val operators = Stack<Char>()
        val tokens = tokenize(expression)

        val precedence = mapOf(
            '+' to 1,
            '-' to 2,
            '*' to 2,
            '/' to 2
        )

        // Shunting Yard Algorithm
        for (token in tokens) {
            when {
                token.matches(Regex("\\d+(\\.\\d+)?")) -> {
                    LogHelper.v("Token numérico detectado: $token")
                    output.add(token) // número
                }
                token.length == 1 && token[0] in precedence -> {
                    LogHelper.v("Operador encontrado: $token")
                    while (operators.isNotEmpty() &&
                        operators.peek() in precedence &&
                        precedence[operators.peek()]!! >= precedence[token[0]]!!
                    ) {
                        output.add(operators.pop().toString())
                    }
                    operators.push(token[0])
                }
                token == "(" -> operators.push('(')
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.peek() != '(') {
                        output.add(operators.pop().toString())
                    }
                    if (operators.isEmpty() || operators.pop() != '(') {
                        LogHelper.e("Erro: Parênteses desbalanceados na expressão")
                        throw IllegalArgumentException("Parênteses desbalanceados")
                    }
                }
            }
        }
        while (operators.isNotEmpty()) {
            val op = operators.pop()
            if (op == '(') {
                LogHelper.e("Erro: Parênteses desbalanceados no final da expressão")
                throw IllegalArgumentException("Parênteses desbalanceados")
            }
            output.add(op.toString())
        }

        // Avaliar a expressão em Notação Polonesa Reversa
        val stack = Stack<Double>()
        for (token in output) {
            when {
                token.matches(Regex("\\d+(\\.\\d+)?")) -> stack.push(token.toDouble())
                token.length == 1 && token[0] in precedence -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = when (token[0]) {
                        '+' -> a + b
                        '-' -> a - b
                        '*' -> a * b
                        '/' -> {
                            if (b == 0.0) {
                                LogHelper.w("Divisão por zero detectada durante avaliação")
                                0.0
                            } else a / b
                        }
                        else -> throw IllegalArgumentException("Operador desconhecido: $token")
                    }
                    LogHelper.v("Resultado parcial: $a ${token[0]} $b = $result")
                    stack.push(result)
                }
            }
        }

        val finalResult = stack.pop()
        LogHelper.i("Expressão avaliada com sucesso. Resultado final: $finalResult")
        return finalResult
    }

    private fun tokenize(expr: String): List<String> {
        val regex = Regex("\\d+(\\.\\d+)?|[()+\\-*/]")
        val tokens = regex.findAll(expr.replace("\\s+".toRegex(), ""))
            .map { it.value }
            .toList()
        LogHelper.v("Tokens extraídos: $tokens")
        return tokens
    }
}