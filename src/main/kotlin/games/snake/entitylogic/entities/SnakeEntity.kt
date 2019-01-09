package games.snake.entitylogic.entities

import abstracted.entity.MovingEntity
import abstracted.ui.`if`.ASCIISupport
import models.Position
import models.SimpleQube

/**
 * Represents a player controllable snake
 */
class SnakeEntity(pos:Position): MovingEntity(pos),ASCIISupport {

    override fun blocks(pos: Position): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun occupiesSimple(): List<SimpleQube> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOccupyRepresentation(pos: Position): Char {
        return '$'.toChar()
    }
}