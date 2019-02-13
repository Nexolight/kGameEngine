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
 * A simple 3d Qube that does not have any rotation
 */
open class SimpleQube(
        val pos:Position = Position(0,0,0),
        val size:Size = Size(0,0,0),
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

    /**
     * Returns an array of points along the surface of the qube
     * TODO: rotation
     */
    fun toAtomicSurface():ArrayList<Position>{
        val xOff = (pos.x/BaseUnits.ONE).toInt()
        val yOff = (pos.y/BaseUnits.ONE).toInt()
        val zOff = (pos.z/BaseUnits.ONE).toInt()
        val bheight = (size.height/BaseUnits.ONE).toInt()
        val bwidth = (size.width/BaseUnits.ONE).toInt()
        val bdepth = (size.depth/BaseUnits.ONE).toInt()

        val aS:ArrayList<Position> = ArrayList<Position>();
        for(x in 0..(size.width/BaseUnits.ONE).toInt()){
            for(y in 0..(size.height/BaseUnits.ONE).toInt()){
                aS.add(Position(x+xOff,y+yOff,zOff+bdepth))//back wall
            }
        }
        for(x in 0..(size.width/BaseUnits.ONE).toInt()){
            for(y in 0..(size.height/BaseUnits.ONE).toInt()){
                aS.add(Position(x+xOff,y+yOff,zOff))//front wall
            }
        }
        for(y in 0..(size.height/BaseUnits.ONE).toInt()){
            for(z in 0..(size.depth/BaseUnits.ONE).toInt()){
                aS.add(Position(xOff+bwidth,y+yOff,z+zOff))//right wall
            }
        }
        for(y in 0..(size.height/BaseUnits.ONE).toInt()){
            for(z in 0..(size.depth/BaseUnits.ONE).toInt()){
                aS.add(Position(xOff,y+yOff,z+zOff))//left wall
            }
        }
        for(x in 0..(size.width/BaseUnits.ONE).toInt()){
            for(z in 0..(size.depth/BaseUnits.ONE).toInt()){
                aS.add(Position(x+xOff,yOff+bheight,z+zOff))//top wall
            }
        }
        for(x in 0..(size.width/BaseUnits.ONE).toInt()){
            for(z in 0..(size.depth/BaseUnits.ONE).toInt()){
                aS.add(Position(x+xOff,yOff,z+zOff))//bottom wall
            }
        }
        return aS
    }

    override fun toString():String{
        return "${size.toString()} | ${pos.toString()}}"
    }
}

/**
 * A simple 3D qube with position size and rotation
 */
class AdvancedQube(
        pos:Position = Position(0,0,0),
        size:Size = Size(0,0,0),
        val rota:Rotation = Rotation(0.0,0.0,0.0),
        pivotOffset:Position = Position(0,0,0)
) : SimpleQube(pos,size,pivotOffset){

    override fun toString():String{
        return "${size.toString()} | ${pos.toString()} | ${rota.toString()}"
    }
}
