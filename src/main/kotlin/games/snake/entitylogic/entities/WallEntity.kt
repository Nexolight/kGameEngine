package games.snake.entitylogic.entities

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import physics.`if`.RigidBody

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

    //Required for serialization
    constructor():super(){}

    constructor(wpos:Position, size:Size = Size(0,0,0), rotation:Rotation) : super(wpos,rotation) {
        occupies.add(AdvancedQube(super.position, size, rotation))
    }

    override fun intersectsRigidBody(pos: Position): Boolean {
        var inX:Boolean = false
        var inY:Boolean = false
        if(pos.x>=position.x && pos.x<=position.x+occupies[0].size.width){//never out of index
            inX = true
        }
        if(pos.y>=position.y && pos.y<=position.y+occupies[0].size.height){//never out of index
            inX = true
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
}