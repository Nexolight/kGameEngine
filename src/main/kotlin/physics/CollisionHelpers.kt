package physics

import models.AdvancedQube
import models.Position
import kotlin.math.abs

/**
 * The way collision detection works in this engine
 * allows for a custom implementation for each entity.
 *
 * An entity may contain multiple 3D objects it consists of.
 *
 *
 * For simplicity these helpers can be used for most cases.
 */
class CollisionHelpers {


    companion object {

        /**
         * Same as the other function except that this takes a list of positions
         * to check.
         */
        fun intersectsPosAQ(pos: List<Position>,myQubes:List<AdvancedQube> ): Boolean {
            for(p in pos){
                if(intersectsPosAQ(p,myQubes)){
                    return true
                }
            }
            return false
        }

        /**
         * Supposed to return true if a given position intersects with
         * one in a given list
         *
         * Can be used by Entities that use positional colliders
         *
         * TODO: Implement rotation
         */
        fun intersectsPosAQ(pos: Position, myQubes:List<AdvancedQube> ): Boolean {
            for(qube:AdvancedQube in myQubes){
                var inX:Boolean = false
                var inY:Boolean = false
                var inZ:Boolean = false
                if(pos.x>=qube.pos.x && pos.x<qube.pos.x+qube.size.width){
                    inX = true
                }
                if(pos.y>=qube.pos.y && pos.y<qube.pos.y+qube.size.height){
                    inY = true
                }
                if(pos.z>=qube.pos.z && pos.z<qube.pos.z+qube.size.depth){
                    inZ = true
                }
                if(inX && inY && inZ){
                    return true
                }
            }
            return false
        }

        /**
         * Supposed to return true if a given qube intersects with
         * one in the given list.
         *
         * Can be used by Entities that use orthogon colliders
         *
         * TODO: This implementation doesn't work yet
         */
        fun intersectAQAQ(foreignQube: AdvancedQube,myQubes:List<AdvancedQube> ): Boolean {
            return false
            val qFrontPrX:Double = foreignQube.size.width//*Math.cos(qQube.rota.yAxis)
            val qFrontPrY:Double = foreignQube.size.height//*Math.cos(qQube.rota.xAxis)
            val qTopPrX:Double = foreignQube.size.width//*Math.cos(qQube.rota.zAxis)
            val qTopPrZ:Double = foreignQube.size.depth//*Math.cos(qQube.rota.xAxis)
            val qLeftPrY:Double = foreignQube.size.height//*Math.cos(qQube.rota.zAxis)
            val qLeftPrZ:Double = foreignQube.size.depth//*Math.cos(qQube.rota.yAxis)
            val qPivot = Position(
                    (foreignQube.size.width)/2+foreignQube.pos.x,
                    (foreignQube.size.height)/2+foreignQube.pos.y,
                    (foreignQube.size.depth)/2+foreignQube.pos.z)

            for(myQube: AdvancedQube in myQubes){
                val mqFrontPrX:Double = myQube.size.width//*Math.cos(myQube.rota.yAxis)
                val mqFrontPrY:Double = myQube.size.height//*Math.cos(myQube.rota.xAxis)
                val mqTopPrX:Double = myQube.size.width//*Math.cos(myQube.rota.zAxis)
                val mqTopPrZ:Double = myQube.size.depth//*Math.cos(myQube.rota.xAxis)
                val mqLeftPrY:Double = myQube.size.height//*Math.cos(myQube.rota.zAxis)
                val mqLeftPrZ:Double = myQube.size.depth//*Math.cos(myQube.rota.yAxis)
                val mqPivot = Position(
                        (myQube.size.width)/2+myQube.pos.x,
                        (myQube.size.height)/2+myQube.pos.y,
                        (myQube.size.depth)/2+myQube.pos.z)
                var inTop = false
                var inFront = false
                var inLeft = false


                val pXAvg:Double=(qPivot.x+mqPivot.x)/2
                val pYAvg:Double=(qPivot.y+mqPivot.y)/2
                val pZAvg:Double=(qPivot.z+mqPivot.z)/2

                /**
                 * Get the center between 2 pivots, make that
                 * positive, and see if it is bigger than the
                 * center to surface distance for the projection
                 */
                if(
                        (abs(pXAvg) - qFrontPrX/2 <= 0|| abs(pXAvg) - mqFrontPrX/2 <= 0) &&
                        (abs(pYAvg) - qFrontPrY/2 <= 0|| abs(pYAvg) - mqFrontPrY/2 <= 0)
                ){
                    inFront=true
                }

                if(
                        (abs(pXAvg) - qTopPrX/2 <= 0|| abs(pXAvg) - mqTopPrX/2 <= 0) &&
                        (abs(pZAvg) - qTopPrZ/2 <= 0|| abs(pZAvg) - mqTopPrZ/2 <= 0)
                ){
                    inTop=true
                }

                if(
                        (abs(pYAvg) - qLeftPrY/2 <=0|| abs(pYAvg) - mqLeftPrY/2 <= 0) &&
                        (abs(pZAvg) - qLeftPrZ/2 <=0|| abs(pZAvg) - mqLeftPrZ/2 <= 0)
                ){
                    inLeft=true
                }

                if(inFront){
                    return true
                }

            }
            return false
        }
    }

}