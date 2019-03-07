package games.snake.entitylogic.entities

import abstracted.Entity
import abstracted.entity.MovingEntity
import abstracted.ui.`if`.ASCIISupport
import games.snake.SnakeDefaultParams
import models.*
import physics.CollisionHelpers
import physics.`if`.OrthogonCollider
import physics.`if`.PositionalCollider
import physics.`if`.RigidBody
import java.util.*

/**
 * Represents a player controllable snake
 * TODO: add 3d support
 */
class SnakeEntity: MovingEntity,ASCIISupport,PositionalCollider,RigidBody {

    /**
     * Body representation of the snake
     * The first entry will always be the head at this entities
     * position
     */
    val body:ArrayList<AdvancedQube> = ArrayList<AdvancedQube>()
    private var nextMoveEnlarge:Boolean = false
    private val uuid:String = UUID.randomUUID().toString()

    constructor():super(){}

    constructor(pos:Position, rota:Rotation):super(pos, rota){
        body.add(AdvancedQube(
                Position().pasteFrom(pos),
                Size(1,1,1),
                Rotation(0.0,0.0,0.0)))
    }

    /**
     * Feed to snake causing it to grow
     */
    fun feed(){
        nextMoveEnlarge=true
    }


    override fun onTransform(posTransform: Position) {
        /**
         * This moves the head into the given direction
         * and each segment to the position of the one
         * in front.
         */
        val previousAQ:AdvancedQube = AdvancedQube(
                Position(0,0,0),
                Size(0,0,0),
                Rotation(0.0,0.0,0.0))
        val currentSA:AdvancedQube = AdvancedQube(
                Position(0,0,0),
                Size(0,0,0),
                Rotation(0.0,0.0,0.0))

        for((bIndex:Int,bQube:AdvancedQube) in body.iterator().withIndex()){
            if(bIndex > 0){// trail/body

                currentSA.pos.pasteFrom(bQube.pos)
                currentSA.rota.pasteFrom(bQube.rota)
                currentSA.size.pasteFrom(bQube.size)

                bQube.pos.pasteFrom(previousAQ.pos)
                bQube.rota.pasteFrom(previousAQ.rota)
                bQube.size.pasteFrom(previousAQ.size)

                previousAQ.pos.pasteFrom(currentSA.pos)
                previousAQ.rota.pasteFrom(currentSA.rota)
                previousAQ.size.pasteFrom(currentSA.size)


            }else{//Head

                previousAQ.pos.pasteFrom(bQube.pos)
                previousAQ.rota.pasteFrom(bQube.rota)
                previousAQ.size.pasteFrom(bQube.size)

                bQube.pos.transform(posTransform)
            }
        }

        //enlarge when the snake has moved over the foods original location
        if(nextMoveEnlarge){
            body.add(previousAQ)
            nextMoveEnlarge=false
        }
    }

    override fun onTransform(rotaTransform: Rotation) {
        /**
         * Just wanna turn the snakes head here
         */
        body.first().rota.transform(rotaTransform)
    }


    override fun getOccupyRepresentation(pos: Position, rota: Rotation): Char {
        if(body.first().pos.x == pos.x && body.first().pos.y == pos.y){
            return SnakeDefaultParams.asciiHEAD
        }else if(body.last().pos == pos){
            when(rota.zAxis){
                0.0,-0.0 -> return SnakeDefaultParams.asciiTailMvUp
                90.0,-270.0 -> return SnakeDefaultParams.asciiTailMvRight
                180.0,-180.0 -> return SnakeDefaultParams.asciiTailMvDown
                -90.0,270.0 -> return SnakeDefaultParams.asciiTailMvLeft
            }
            return 'X'//broken should not appear

        }
        return SnakeDefaultParams.asciiBODY
    }

    override fun occupies(): List<AdvancedQube> {
        return body
    }

    override fun getColliderPositions(): List<Position> {
        /**
         * We only check the head.
         */
        return Arrays.asList(body.get(0).pos)
    }

    override fun intersectsRigidBody(pos: List<Position>): Boolean {
        val body:ArrayList<AdvancedQube> = body
        body.removeAt(0)
        return CollisionHelpers.intersectsPosAQ(pos,body)
    }

    override fun intersectsRigidBody(pos: Position): Boolean {
        val body:ArrayList<AdvancedQube> = body
        body.removeAt(0)
        return CollisionHelpers.intersectsPosAQ(pos,body)
    }

    override fun intersectsRigidBody(qQube: AdvancedQube): Boolean {
        //TODO: replace the positional collider when this works.
        return false
        //return CollisionHelpers.intersectAQAQ(qQube,occupies)
    }

    /*
    override fun getColliderBlocks(): List<AdvancedQube> {
        return Arrays.asList(body.get(0))
    }*/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SnakeEntity

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }


}


