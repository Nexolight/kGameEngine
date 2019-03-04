package abstracted.entity.presets

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import physics.CollisionHelpers
import physics.`if`.RigidBody
import java.util.*

/**
 * Represents a empty surface
 */
class BGEntity: StaticEntity, ASCIISupport {

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

    override fun occupies(): List<AdvancedQube> {
        return occupies
    }

    override fun getOccupyRepresentation(pos: Position, rota: Rotation): Char {
        return ' '
    }

}