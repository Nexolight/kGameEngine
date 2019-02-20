package abstracted

import models.Buff
import models.Position
import models.Rotation
import java.util.*

/**
 * Generic class that deals with any
 * objects inside a map
 */
abstract class Entity{
    val position:Position
    val rotation:Rotation
    val buffs: Deque<Buff> = LinkedList<Buff>()

    constructor(position:Position = Position(0,0,0),
                rotation:Rotation = Rotation(0.0,0.0,0.0)){
        this.position=position
        this.rotation=rotation
    }
}