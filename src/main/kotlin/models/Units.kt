package models

/**
 * The effective value of an internal
 * game unit.
 */
class BaseUnits{
    companion object {
        val ONE:Double = 25.00
        val FLAT:Double = 0.0001 //Intended for text panes TODO: evaluate
    }
}

class SquareUnits{
    companion object {
        val SQUARE:Pair<Int,Double> = Pair<Int,Double>(2,BaseUnits.ONE)
    }
}

/**
 * A simple 3D qube with position and size
 */
class AdvancedQube(
        val pos:Position = Position(0,0,0),
        val size:Size = Size(0,0,0),
        val rota:Rotation = Rotation(0.0,0.0,0.0),
        val pivotOffset:Position = Position(0,0,0)
){

    /**
     * Returns the pivot point
     */
    fun getPivot():Position{
        val pivot:Position = Position(0,0,0)
        pivot.x = (pos.x/2)+pivotOffset.x
        pivot.y = (pos.y/2)+pivotOffset.y
        pivot.z = (pos.z/2)+pivotOffset.z
        return pivot
    }

    override fun toString():String{
        return "${size.toString()} | ${pos.toString()} | ${rota.toString()}"
    }
}
