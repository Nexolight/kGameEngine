package models

class BaseUnits{
    companion object {
        val ONE:Double = 25.00
    }
}

class SquareUnits{
    companion object {
        val SQUARE:Pair<Int,Double> = Pair<Int,Double>(2,BaseUnits.ONE)
    }
}

class SimpleRect(pos:Position){
    val pos = pos
    val t:Double = BaseUnits.ONE
    val r:Double = BaseUnits.ONE
}