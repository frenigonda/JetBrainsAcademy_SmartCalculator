import java.math.BigInteger

const val VAR_PATTERN = "^([0-9]+|[a-zA-Z]+)\\z"
const val OPER2_PATTERN = "^(\\+|-)+\\z"
const val OPER1_PATTERN = "^(\\*|\\/){1}\\z"

class PolishNotationConverter {
    // - when we get a number - output it
    // - when we get an operator O, pop the top element in the stack
    // until there is no operator having higher priority then O
    // and then push(O) into the stack
    // - when the expression is ended, pop all the operators remain in the stack
    fun convert(input: String): List<String> {
        val stack = mutableListOf<String>()
        val result = mutableListOf<String>()
        var tmpVar = ""
        var tmpO = ""

        fun addVarToResult(v: String) {
            if (v.isEmpty()) { return }
            val regex = VAR_PATTERN.toRegex()
            if (regex.find(v) != null) {
                result.add(v)
                tmpVar = ""
            } else {
                throw Exception("Invalid expression")
            }
        }

        fun addOperToResult(o: String) {
            if (o.isEmpty()) { return }
            val r1 = OPER1_PATTERN.toRegex()
            val r2 = OPER2_PATTERN.toRegex()
            if (o == "^") {
                stack.add(o)
                tmpO = ""
            } else if (r1.find(o) != null) {
                while (stack.isNotEmpty() && stack.last() == "^") {
                    result.add(stack.last())
                    stack.removeLast()
                }
                stack.add(o)
                tmpO = ""
            } else if (r2.find(o) != null) {
                if (o.replace(oldValue = "+", newValue = "").count() % 2 == 0) {
                    tmpO = "+"
                } else {
                    tmpO = "-"
                }
                while (stack.isNotEmpty() && (stack.last() == "*" || stack.last() == "/")) {
                    result.add(stack.last())
                    stack.removeLast()
                }
                while (stack.isNotEmpty() && (stack.last() == "+" || stack.last() == "-")) {
                    result.add(stack.last())
                    stack.removeLast()
                }
                stack.add(tmpO)
                tmpO = ""
            } else {
                throw Exception("Invalid expression")
            }
        }

        for (c in input) {
            when (c) {
                ' ' -> {
                    continue
                }
                '^' -> {
                    addVarToResult(tmpVar)
                    if (tmpO.isEmpty()) {
                        tmpO += c
                    } else {
                        throw Exception("Invalid expression")
                    }
                }
                in '0'..'9' -> {
                    addOperToResult(tmpO)
                    tmpVar += c
                }
                in 'A'..'z' -> {
                    addOperToResult(tmpO)
                    tmpVar += c
                }
                '+' -> {
                    addVarToResult(tmpVar)
                    tmpO += c
                }
                '-' -> {
                    addVarToResult(tmpVar)
                    tmpO += c
                }
                '*' -> {
                    addVarToResult(tmpVar)
                    if (tmpO.isEmpty()) {
                        tmpO += c
                    } else {
                        throw Exception("Invalid expression")
                    }
                }
                '/' -> {
                    addVarToResult(tmpVar)
                    if (tmpO.isEmpty()) {
                        tmpO += c
                    } else {
                        throw Exception("Invalid expression")
                    }
                }
                '(' -> {
                    addOperToResult(tmpO)
                    addVarToResult(tmpVar)
                    stack.add(c.toString())
                }
                ')' -> {
                    addVarToResult(tmpVar)
                    if (stack.isEmpty() || stack.last() == "(") { throw Exception("Invalid expression") }
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        result.add(stack.last())
                        stack.removeLast()
                    }
                    if (stack.isNotEmpty() && stack.last() == "(") {
                        stack.removeLast()
                    } else {
                        throw Exception("Invalid expression")
                    }
                }
                else -> {
                    throw Exception("Invalid expression")
                }
            }
        }
        if (tmpVar.isNotEmpty()) {
            if (tmpO.isNotEmpty()) { throw Exception("Invalid expression") }
            addVarToResult(tmpVar)
        }
        while (stack.isNotEmpty()) {
            if (stack.last() == "(") { throw Exception("Invalid expression") }
            result.add(stack.last())
            stack.removeLast()
        }
        return result
    }

    fun calculate(pnExp: List<String>, memory: Map<String, String>): String {
        fun strToInt(s: String): BigInteger {
            return try {
                s.toBigInteger()
            } catch (e: Exception) {
                if (memory[s] != null) {
                    memory[s]!!.toBigInteger()
                } else {
                    throw Exception("Unknown variable")
                }
            }
        }

        val stack = mutableListOf<BigInteger>()
        for (e in pnExp) {
            when (e) {
                "^", "+", "-", "*", "/" -> {
                    if (stack.count() < 1) { throw Exception("Invalid expression") }
                    val b = stack.last()
                    stack.removeLast()
                    val a: BigInteger = try {
                        stack.last()
                        stack.removeLast()
                    } catch (exeption: Exception) {
                        if (e == "-" || e == "+") {
                            0.toBigInteger()
                        } else {
                            throw Exception("Invalid expression")
                        }
                    }

                    when (e) {
                        "^" -> {
                            var power: BigInteger = a
                            var i = 0.toBigInteger()
                            while (i < b) {
                                power *= a
                                i++
                            }
                            stack.add(power)
                        }
                        "+" -> {
                            stack.add(a + b)
                        }
                        "-" -> {
                            stack.add(a - b)
                        }
                        "*" -> {
                            stack.add(a * b)
                        }
                        "/" -> {
                            stack.add(a / b)
                        }
                        else -> {
                            throw Exception("Unknown variable")
                        }
                    }
                }
                else -> {
                    stack.add(strToInt(e))
                }
            }
        }

        return stack.first().toString()
    }
}