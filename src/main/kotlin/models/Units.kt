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

class SimpleQube(pos:Position,widthBaseUnits:Int=0,heightBaseUnits:Int=0,depthBaseUnits:Int=0){
    val pos = pos
    val width:Double = widthBaseUnits*BaseUnits.ONE
    val height:Double = heightBaseUnits*BaseUnits.ONE
    val depth:Double = depthBaseUnits*BaseUnits.ONE

    override fun toString():String{
        return "Block: width: $width, height: $height, depth: $depth"
    }
}