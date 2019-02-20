package games.snake.entitylogic.entities

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import games.snake.SnakeBuffs
import models.AdvancedQube
import models.Position
import models.Rotation
import models.Size
import physics.CollisionHelpers
import physics.`if`.RigidBody
import java.util.*

class EdibleEntity: StaticEntity, ASCIISupport, RigidBody {

    var wasEaten = false

    private var occupies: ArrayList<AdvancedQube> = ArrayList<AdvancedQube>()
    private val uuid:String = UUID.randomUUID().toString()

    //Required for serialization
    constructor():super(){}

    constructor(wpos:Position, size: Size = Size(1,1,1), rotation:Rotation = Rotation()) : super(wpos,rotation) {
        occupies.add(AdvancedQube(super.position, size, rotation))
    }

    override fun getOccupyRepresentation(pos: Position, rota: Rotation): Char {
        when(buffs.first){
            SnakeBuffs.food -> return '*'
            SnakeBuffs.speeddownM -> return '-'
            SnakeBuffs.speeddownL -> return '!'
            SnakeBuffs.speedupM -> return '+'
            SnakeBuffs.speedupL -> return '^'
        }
        return 'X' //not supposed to show up
    }

    override fun occupies(): List<AdvancedQube> {
        return occupies
    }

    override fun intersectsRigidBody(pos: Position): Boolean {
        return CollisionHelpers.intersectsPosAQ(pos,occupies)
    }

    override fun intersectsRigidBody(pos: List<Position>): Boolean {
        for(p in pos){
            if(intersectsRigidBody(p)){
                return true
            }
        }
        return false
    }

    override fun intersectsRigidBody(qQube: AdvancedQube): Boolean {
        //TODO: replace the positional collider when this works.
        return false
        //return CollisionHelpers.intersectAQAQ(qQube,occupies)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EdibleEntity

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}