import kotlin.math.exp
import kotlin.math.pow


const val ARG_SMALL_INCREMENT = 1.2E-4
// With this parameter value, derivatives are calculated with an accuracy of 8 significant digits

fun calculateDerivatives(doubleFunction: (DoubleArray) -> Double, doubleVector: (DoubleArray)): Map<String, Double> {

    val dim = doubleVector.size

    val firstPartialDerivatives = List(dim) { index ->
        { vector: DoubleArray -> partialDerivative(doubleFunction, index, vector) }
    }

    val namedMapOfFirstPartialDerivativesAtPoint =
        firstPartialDerivatives.mapIndexed { index: Int, firstPartialDerivative: (DoubleArray) -> Double ->
            Pair(
                "∂f/∂x_${index + 1}",
                firstPartialDerivative(doubleVector)
            )
        }.toMap()


    val namedMapOfSecondPartialDerivativesAtPoint =
        firstPartialDerivatives.mapIndexed { firstDerivativeIndex: Int, firstDerivative: (DoubleArray) -> Double ->
            List(dim) { secondDerivativeIndex: Int ->
                Pair(
                    "∂2f/∂x_${secondDerivativeIndex + 1}∂x_${firstDerivativeIndex + 1}",
                    partialDerivative(firstDerivative, secondDerivativeIndex, doubleVector)
                )
            }
        }.reduce { accListOfPairs, pair -> accListOfPairs + pair }.toMap()


    return namedMapOfFirstPartialDerivativesAtPoint + namedMapOfSecondPartialDerivativesAtPoint
}


fun printMapOfValuesNamesToValue(map: Map<String, Double>) = map.forEach { println("${it.key} = ${it.value}") }


fun partialDerivative(doubleFunction: (DoubleArray) -> Double, index: Int, doubleVector: DoubleArray): Double {
    val dim = doubleVector.size

    val doubleVectorWithPartialIncrement = { dx: Double ->
        DoubleArray(dim) { if (it == index) doubleVector[it] + dx else doubleVector[it] }
    }

    val functionDoubledPartialIncrement = { dx: Double ->
        doubleFunction(doubleVectorWithPartialIncrement(dx)) - doubleFunction(doubleVectorWithPartialIncrement(-dx))
    }

//    val differenceBetweenFunDoubledIncrementAndItsLinearPart = { dx: Double, partialDerivative: Double ->
//        functionDoubledPartialIncrement(dx) - 2 * partialDerivative * dx
//    }
//
//    val differenceBetweenFunIncrementAndItsLinearPartWithFixedArgIncrement = { partialDerivative: Double ->
//        differenceBetweenFunDoubledIncrementAndItsLinearPart(ARG_SMALL_INCREMENT, partialDerivative)
//    }

    val derivativeApproximation = { dx: Double ->
        functionDoubledPartialIncrement(dx) / (2 * dx)
    }


//    print(
//        binarySearchOfZeros(
//            differenceBetweenFunIncrementAndItsLinearPartWithFixedArgIncrement,
//            -MAX_VALUE_OF_DERIVATIVES,
//            MAX_VALUE_OF_DERIVATIVES,
//            DIFFERENTIAL_ACCURACY
//        )
//    )

    return derivativeApproximation(ARG_SMALL_INCREMENT)
}


//fun binarySearchOfZeros(
//    function: (Double) -> Double, leftBorder: Double, rightBorder: Double, accuracy: Double
//): Double {
//    var varLeftBorder = leftBorder
//    var varRightBorder = rightBorder
//    var varCenter = (leftBorder + rightBorder) / 2
//
//    while (!doubleEqualWithAccuracy(function(varCenter), 0.0, accuracy)) {
//        when {
//            sign(function(varCenter)) != sign(function(varLeftBorder)) -> {
//                varRightBorder = varCenter
//                varCenter = (varLeftBorder + varRightBorder) / 2
//                continue
//            }
//
//            sign(function(varCenter)) != sign(function(varRightBorder)) -> {
//                varLeftBorder = varCenter
//                varCenter = (varLeftBorder + varRightBorder) / 2
//                continue
//            }
//
//            else -> {
//                println(sign(function(varLeftBorder)))
//                println(sign(function(varCenter)))
//                println(sign(function(varRightBorder)))
//                throw Exception("No way")
//            }
//        }
//    }
//    return varCenter
//}
//
//
//fun doubleEqualWithAccuracy(value1: Double, value2: Double, accuracy: Double): Boolean {
//    return kotlin.math.abs(value1 - value2) <= accuracy
//}


fun readPointFromConsole(): DoubleArray {
    return readln().split(" ").map { it.toDouble() }.toDoubleArray()
}


fun userFun(doubleVector: DoubleArray): Double {
    return exp(doubleVector.sum().pow(-2) + exp(doubleVector[0] - 1).pow(3))
}


fun main() {

    val point = readPointFromConsole()
    val derivativesAtPoint = calculateDerivatives(::userFun, point)

    printMapOfValuesNamesToValue(derivativesAtPoint)
}
