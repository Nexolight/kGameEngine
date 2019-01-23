package abstracted.entity

import abstracted.Entity
import models.Position
import models.Rotation

/**
 * Describes a static (non-moving) entity inside the world
 */
abstract class StaticEntity:Entity{
    constructor():super(){}
    constructor(pos:Position,rota:Rotation):super(pos,rota){}
}