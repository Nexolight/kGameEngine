package games.snake.entitylogic.entities

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import physics.`if`.RigidBody
import java.util.*
import kotlin.math.abs

/**
 * Represents a wall
 * TODO: add 3d support
 */
class WallEntity: StaticEntity, ASCIISupport, RigidBody {

    /**
     * Will only contain one entry
     * but is used by abstract methods
     */
    var occupies:ArrayList<AdvancedQube> = ArrayList<AdvancedQube>()
    private val uuid:String = UUID.randomUUID().toString()

    //Required for serialization
    constructor():super(){}

    constructor(wpos:Position, size:Size = Size(0,0,0), rotation:Rotation) : super(wpos,rotation) {
        occupies.add(AdvancedQube(super.position, size, rotation))
    }


    override fun intersectsRigidBody(pos: List<Position>): Boolean {
        for(p in pos){
            if(intersectsRigidBody(p)){
                return true
            }
        }
        return false
    }

    override fun intersectsRigidBody(pos: Position): Boolean {
        var inX:Boolean = false
        var inY:Boolean = false
        if(pos.x>=occupies[0].pos.x && pos.x<occupies[0].pos.x+occupies[0].size.width){//never out of index
            inX = true
        }
        if(pos.y>=occupies[0].pos.y && pos.y<occupies[0].pos.y+occupies[0].size.height){//never out of index
            inY = true
        }
        if(inX && inY){
            return true
        }
        return false
    }

    override fun intersectsRigidBody(qQube: AdvancedQube): Boolean {
        return false//TODO:fix that
        val qFrontPrX:Double = qQube.size.width//*Math.cos(qQube.rota.yAxis)
        val qFrontPrY:Double = qQube.size.height//*Math.cos(qQube.rota.xAxis)
        val qTopPrX:Double = qQube.size.width//*Math.cos(qQube.rota.zAxis)
        val qTopPrZ:Double = qQube.size.depth//*Math.cos(qQube.rota.xAxis)
        val qLeftPrY:Double = qQube.size.height//*Math.cos(qQube.rota.zAxis)
        val qLeftPrZ:Double = qQube.size.depth//*Math.cos(qQube.rota.yAxis)
        val qPivot = Position(
                (qQube.size.width)/2+qQube.pos.x,
                (qQube.size.height)/2+qQube.pos.y,
                (qQube.size.depth)/2+qQube.pos.z)

        for(myQube:AdvancedQube in occupies){
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


    override fun occupies(): List<AdvancedQube> {
        return occupies
    }

    override fun getOccupyRepresentation(pos: Position, rota: Rotation): Char {
        return '#'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WallEntity

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}