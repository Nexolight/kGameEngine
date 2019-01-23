package physics.`if`

import abstracted.Entity
import models.SimpleQube

/**
 * A collider interface that can be
 * used for any rectangular shape
 */
interface OrthogonCollider {

    /**
     * Returns a list of colliding rectangles
     * that will be checked for collision
     */
    fun getColliderBlocks():List<SimpleQube>

    /**
     * If a collision happens this will be called
     * as a feedback on which of the collider blocks
     * the collision happened and with which entity it
     * has collided
     */
    fun onOrthogonalCollide(collisionOn: SimpleQube, collidingWith:Entity)
}