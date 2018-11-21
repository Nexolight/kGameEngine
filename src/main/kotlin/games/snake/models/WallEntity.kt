package games.snake.models

import abstracted.entity.StaticEntity
import models.BaseUnits
import models.Position
import models.SimpleRect

/**
 * Represents a wall
 */
class WallEntity: StaticEntity{
    var width:Double
    var height:Double
    var occupies:MutableList<SimpleRect> = ArrayList<SimpleRect>()

    constructor(wpos:Position, width:Int=1, height:Int=1) : super(wpos) {
        this.width=width*BaseUnits.ONE
        this.height=height*BaseUnits.ONE

        for(x in 0 until width){
            occupies.add(SimpleRect(Position(x,0,0)))
            occupies.add(SimpleRect(Position(x,height,0)))
        }
        for(y in 1 until height-1){
            occupies.add(SimpleRect(Position(0,y,0)))
            occupies.add(SimpleRect(Position(width,y,0)))
        }
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

    override fun occupiesSimple(): List<SimpleRect> {
        return occupies
    }
}