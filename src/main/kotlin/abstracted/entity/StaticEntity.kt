package abstracted.entity

import abstracted.Entity
import models.Position

/**
 * Describes a static (non-moving) entity inside the world
 */
abstract class StaticEntity(pos:Position) : Entity(pos){

}