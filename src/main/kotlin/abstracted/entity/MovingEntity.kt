package abstracted.entity

import abstracted.Entity
import abstracted.logic.EntityLogic
import models.Position

/**
 * Describes a moving entity inside the world
 */
abstract class MovingEntity(pos:Position) : Entity(pos){

    /**
     * Move the entity by the given position offset
     */
    fun move(transform:Position){
        super.position.transform(transform)
    }

}