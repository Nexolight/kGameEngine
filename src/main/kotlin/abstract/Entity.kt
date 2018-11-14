package abstract

import abstract.logic.DummyLogic
import flow.ActionHandler
import models.Buff
import models.Position
import java.util.*

/**
 * Generic class that deals with any
 * objects inside a map
 */
abstract class Entity(ah:ActionHandler, position:Position = Position(), logic:Logic = DummyLogic(ah)){
    protected val buffs: Deque<Buff> = LinkedList<Buff>()
}