package abstracted

import abstracted.logic.DummyLogic
import abstracted.logic.EntityLogic
import models.Buff
import models.Position
import models.SimpleRect
import java.util.*

/**
 * Generic class that deals with any
 * objects inside a map
 */
abstract class Entity(position:Position = Position(), logic:EntityLogic = DummyLogic()){
    protected val position:Position = position
    protected val buffs: Deque<Buff> = LinkedList<Buff>()

    /**
     * Returns true if the entity blocks the given position
     */
    abstract fun blocks(pos:Position):Boolean

    /**
     * Returns a simplified list with blocks
     * that the entity occupies
     */
    abstract fun occupiesSimple():List<SimpleRect>
}