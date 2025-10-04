package com.example.hub.screens.calculator
import java.util.Stack

object SimpleMathEvaluator {
    fun eval(expression: String): Double {
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
                token.matches(Regex("\\d+(\\.\\d+)?")) -> output.add(token) // número
                token.length == 1 && token[0] in precedence -> {
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
                        throw IllegalArgumentException("Parênteses desbalanceados")
                    }
                }
            }
        }
        while (operators.isNotEmpty()) {
            val op = operators.pop()
            if (op == '(') throw IllegalArgumentException("Parênteses desbalanceados")
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
                        '/' -> a / b
                        else -> throw IllegalArgumentException("Operador desconhecido: $token")
                    }
                    stack.push(result)
                }
            }
        }

        return stack.pop()
    }

    private fun tokenize(expr: String): List<String> {
        val regex = Regex("\\d+(\\.\\d+)?|[()+\\-*/]")
        return regex.findAll(expr.replace("\\s+".toRegex(), ""))
            .map { it.value }
            .toList()
    }
}