package games.snake.entitylogic.entities

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import physics.CollisionHelpers
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
        return CollisionHelpers.intersectsPosAQ(pos,Arrays.asList(occupies[0]))
    }

    override fun intersectsRigidBody(pos: Position): Boolean {
        return CollisionHelpers.intersectsPosAQ(pos,Arrays.asList(occupies[0]))
    }

    override fun intersectsRigidBody(qQube: AdvancedQube): Boolean {
        //TODO: replace the positional collider when this works.
        return false
        //return CollisionHelpers.intersectAQAQ(qQube,occupies)
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