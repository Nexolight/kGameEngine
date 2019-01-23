package abstracted.entity

import abstracted.Entity
import models.Position
import models.Rotation

/**
 * Describes a moving entity inside the world
 */
abstract class MovingEntity : Entity{

    constructor():super(){}
    constructor(pos:Position,rota:Rotation):super(pos,rota){}

    /**
     * Move the entity by the given position offset
     * and inform the specific implementation about it
     */
    fun transform(posTransform:Position){
        super.position.transform(posTransform)
        onTransform(posTransform)
    }

    /**
     * Move the entity by the given rotation offset
     * and inform the specific implementation about it
     */
    fun transform(rotaTransform: Rotation){
        super.rotation.transform(rotaTransform)
        onTransform(rotaTransform)
    }

    /**
     * This returns the current rotation values
     * you're supposed to *-1 and paste these
     * values to reset the transformation.
     *
     * You cannot set it directly because of
     * potential transform animations
     */
    fun getRotationValues():Rotation{
        val ret:Rotation = Rotation(0.0,0.0,0.0)
        ret.pasteFrom(super.rotation)
        return ret
    }

    /**
     * This returns the current position values
     * you're supposed to *-1 and paste these
     * values to reset the transformation.
     *
     * You cannot set it directly because of
     * potential transform animations
     */
    fun getPositionValues():Position{
        val ret:Position = Position(0,0,0)
        ret.pasteFrom(super.position)
        return ret
    }

    /**
     * This is called when the position of the entity was transformed.
     * Implement this to move other objects that are bound/attached to the
     * entity in some way.
     */
    protected abstract fun onTransform(posTransform:Position)

    /**
     * This is called when the position of the entity was transformed.
     * Implement this to move other objects that are bound/attached to the
     * entity in some way.
     */
    protected abstract fun onTransform(rotaTransform:Rotation)

}