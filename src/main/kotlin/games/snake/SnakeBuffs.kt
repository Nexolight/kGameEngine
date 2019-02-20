package games.snake

import models.Buff

/**
 * Describes buffs used in the game
 */
class SnakeBuffs {
    companion object {
        val food: Buff = Buff("Food for the snake",100,1.0)
        val speedupM = Buff("Medium speed up buff",101,1.2)
        val speeddownM = Buff("Medium speed dpwn buff",102,0.8)
        val speedupL = Buff("Large speed up buff",103,1.4)
        val speeddownL = Buff("Large speed down buff",104,0.6)
    }
}
