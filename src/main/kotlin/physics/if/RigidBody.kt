package physics.`if`

import models.Position

/**
 * Entities that want collision on them need
 * need to implement this
 */
interface RigidBody {

    /**
     * Returns true when the given position
     * is inside of a rigid body
     */
    fun intersectsRigidBody(pos: Position): Boolean

    /**
     * Returns true if one of the given positions is
     * inside of a rigid body
     */
    fun intersectsRigidBody(pos: List<Position>): Boolean
}