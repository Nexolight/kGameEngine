package games.snake.entitylogic.entities

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import physics.`if`.RigidBody
import java.util.*

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