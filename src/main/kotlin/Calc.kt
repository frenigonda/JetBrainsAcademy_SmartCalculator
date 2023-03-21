import java.util.Scanner

const val COM_PATTERN = "^/[a-z]*\\z"
const val NAME_PATTERN = "^[a-zA-Z]+\\z"
const val BANNED_SYMBOLS = "[^a-zA-Z0-9+\\-*/^=()]"

class Calc {
    fun main() {
        val reader = Scanner(System.`in`)
        val removeSpaces = "\\s".toRegex()
        while (reader.hasNext()) {
            val input = reader.nextLine().replace(regex = removeSpaces, replacement = "")
            if (input.isEmpty()) { continue }

            if (BANNED_SYMBOLS.toRegex().containsMatchIn(input)) {
                println("Invalid expression")
                continue
            }

            when (checkInput(input)) {
                InputType.HELP -> {
                    println("Allowed symbols: 0-9, a-z, A-Z, +, -, *, /, ^, (, ), = and spaces")
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
                memory[expressionParts[0]] = res
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