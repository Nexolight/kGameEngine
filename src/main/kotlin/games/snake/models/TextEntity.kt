package games.snake.models

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.BaseUnits
import models.Position
import models.SimpleQube

/**
 * This entity can be used to display text on the field
 */
class TextEntity(pos:Position) : StaticEntity(pos), ASCIISupport{
    private var text:String = ""
    private var cols:Int = 0
    private var occupies:MutableList<SimpleQube> = ArrayList<SimpleQube>()
    private var chars:HashMap<Position,Char> = HashMap<Position,Char>()

    /**
     * Update the text for this entity
     */
    fun updateText(newtext:String,cols:Int){
        occupies.clear()
        chars.clear()
        var col:Int = (super.position.x/BaseUnits.ONE).toInt()
        var row:Int = (super.position.y/BaseUnits.ONE).toInt()
        for(chr:Char in newtext){
            if(chr == '\n'){//catch newlines
                ++col
            }
            val chrpos:Position = Position(col,row,0)
            occupies.add(SimpleQube(chrpos,1,1,0))
            chars.put(chrpos,chr)
            if(col == cols){
                col=(super.position.x/BaseUnits.ONE).toInt()
                ++row
            }
            ++col
        }
    }

    override fun blocks(pos: Position): Boolean {
        return false //text shouldn't be blocking at all
    }

    override fun occupiesSimple(): List<SimpleQube> {
        return occupies
    }

    override fun getOccupyRepresentation(pos: Position): Char {
        val chr:Char? = chars.get(pos)
        if(chr != null){
            return chr
        }
        return ' '
    }
}