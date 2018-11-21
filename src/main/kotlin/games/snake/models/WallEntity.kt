package games.snake.models

import abstracted.entity.StaticEntity
import models.BaseUnits
import models.Position
import models.SimpleQube

/**
 * Represents a wall
 */
class WallEntity: StaticEntity{
    var width:Double
    var height:Double
    var occupies:MutableList<SimpleQube> = ArrayList<SimpleQube>()

    constructor(wpos:Position, iwidth:Int=1, iheight:Int=1) : super(wpos) {
        this.width=iwidth*BaseUnits.ONE
        this.height=iheight*BaseUnits.ONE

        for(x in 0 until iwidth){
            occupies.add(SimpleQube(Position(x,0,0)))
            occupies.add(SimpleQube(Position(x,iheight,0)))
        }
        for(y in 1 until iheight-1){
            occupies.add(SimpleQube(Position(0,y,0)))
            occupies.add(SimpleQube(Position(iwidth,y,0)))
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

    override fun occupiesSimple(): List<SimpleQube> {
        return occupies
    }
}