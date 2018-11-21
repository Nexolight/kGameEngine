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

class SimpleQube(pos:Position){
    val pos = pos
    val width:Double = BaseUnits.ONE
    val height:Double = BaseUnits.ONE
    val depth:Double = BaseUnits.ONE
}