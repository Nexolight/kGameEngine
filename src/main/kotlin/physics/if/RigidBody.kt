package physics.`if`

import models.AdvancedQube
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

    /**
     * Returns true of the given AdvancedQube touches
     * the rigid body
     */
    fun intersectsRigidBody(qQube: AdvancedQube): Boolean

    /**
     * Collision Detection is done Async
     * in this game engine due to performance
     * reasons.
     *
     * Meaning it is calculated on a serialized
     * snapshot after all Action Requests are done.
     *
     * In order to detect if the collision is
     * relevant the equals method must be overriden
     * to return true on the realtime object and the
     * serialized one.
     */
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}