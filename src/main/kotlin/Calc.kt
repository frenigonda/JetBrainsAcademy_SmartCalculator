import java.util.Scanner

const val COM_PATTERN = "^/[a-z]*\\z"
const val NAME_PATTERN = "^(\\s*[a-zA-Z]+\\s*){1}\\z"

class Calc {
    fun main() {
        val reader = Scanner(System.`in`)
        while (reader.hasNext()) {
            val input = reader.nextLine()
            if (input.isEmpty()) { continue }

            when (checkInput(input)) {
                InputType.HELP -> {
                    println("The program calculates the sum of numbers")
                    continue
                }
                InputType.UNKNOWN_C -> {
                    println("Unknown command")
                    continue
                }
                InputType.EXIT -> {
                    println("Bye!")
                    break
                }
                InputType.EXPRESSION -> {
                    try {
                        handleInput(input)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                    continue
                }
                else -> {
                    println("Invalid expression")
                }
            }
        }
    }

    private fun checkInput(input: String): InputType {
        val comRegex = COM_PATTERN.toRegex()
        if (comRegex.find(input) != null) {
            return when (input) {
                "/help" -> {
                    InputType.HELP
                }

                "/exit" -> {
                    InputType.EXIT
                }

                else -> {
                    InputType.UNKNOWN_C
                }
            }
        }
        return InputType.EXPRESSION
    }

    private fun handleInput(input: String) {
        //(TODO): check for banned symbols
        val expressionParts = input.split("=")
        when (expressionParts.count()) {
            0 -> {
                // do nothing
                return
            }
            1 -> {
                println(pnConverter.calculate(pnExp = pnConverter.convert(expressionParts[0]), memory = memory))
            }
            2 -> {
                // check var name for validity (Invalid identifier)
                if (!checkNameValidity(expressionParts[0])) {
                    throw Exception("Invalid identifier")
                }
                // calculate
                val res = pnConverter.calculate(pnExp = pnConverter.convert(expressionParts[1]), memory = memory)
                // assign
                memory[expressionParts[0].replace("\\s".toRegex(), "")] = res
            }
            else -> {
                // Invalid assignment
                throw Exception("Invalid assignment")
            }
        }
    }

    private fun checkNameValidity(v: String): Boolean {
        val regex = NAME_PATTERN.toRegex()
        return regex.find(v) != null
    }

    enum class InputType {
        HELP, EXIT, EXPRESSION, UNKNOWN_C
    }

    private var memory = mutableMapOf<String, String>()
    private val pnConverter = PolishNotationConverter()
}