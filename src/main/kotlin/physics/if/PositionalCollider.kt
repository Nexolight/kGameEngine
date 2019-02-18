package physics.`if`

import models.Position

interface PositionalCollider {

    /**
     * This is supposed to return a list
     * of positions along the surface of the entity
     *
     * If any of these positions end up being inside
     * another 3D Object a collision is triggered
     * via ActionHandler notification that will inform
     * about the source and destination colliding entity
     *
     * Please note that this is the collision
     * at the moment of the serialization.
     * Movement applied after that must be taken into
     * account and corrected accordingly.
     */
    fun getColliderPositions():List<Position>

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
