package physics.`if`

import abstracted.Entity
import abstracted.logic.EntityLogic
import models.AdvancedQube
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
    fun getColliderBlocks():List<AdvancedQube>

}