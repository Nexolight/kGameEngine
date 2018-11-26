package games.snake.models

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.Position
import models.SimpleQube

/**
 * This entity can be used to display text on the field
 */
class TextEntity(pos:Position,text:String) : StaticEntity(pos), ASCIISupport{
    private var text:String = text
    private var occupies:MutableList<SimpleQube> = ArrayList<SimpleQube>()

    /**
     * Update the text for this entity
     */
    fun updateText(newtext:String,cols:Int){
        occupies.clear()
        for(chr:Char in newtext){
            occupies.add(SimpleQube(Position(),1,1,0))
        }
    }

    override fun blocks(pos: Position): Boolean {
        return false
    }

    override fun occupiesSimple(): List<SimpleQube> {
        return occupies
    }

    override fun getOccupyRepresentation(pos: Position): Char {
        return '-'//TODO: return char at string position
    }
}