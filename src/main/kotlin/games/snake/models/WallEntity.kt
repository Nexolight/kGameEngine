package games.snake.models

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import games.snake.SnakeParams
import models.BaseUnits
import models.Position
import models.SimpleQube

/**
 * Represents a wall
 */
class WallEntity: StaticEntity, ASCIISupport {
    var width:Double
    var height:Double
    var occupies:MutableList<SimpleQube> = ArrayList<SimpleQube>()

    constructor(wpos:Position, iwidth:Int=1, iheight:Int=1) : super(wpos) {
        this.width=iwidth*BaseUnits.ONE
        this.height=iheight*BaseUnits.ONE

        occupies.add(SimpleQube(super.position,(width/BaseUnits.ONE).toInt(),(height/BaseUnits.ONE).toInt(),0))
    }

    override fun blocks(pos: Position):Boolean {
        var inX:Boolean = false
        var inY:Boolean = false
        if(pos.x>=position.x && pos.x<=position.x+width){
            inX = true
        }
        if(pos.y>=position.y && pos.y<=position.y+height){
            inX = true
        }
        if(inX && inY){
            return true
        }
        return false
    }

    override fun occupiesSimple(): List<SimpleQube> {
        return occupies
    }

    override fun getOccupyRepresentation(pos: Position): Char {
        return '#'
    }
}