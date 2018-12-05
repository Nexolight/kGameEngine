package games.snake.models

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.BaseUnits
import models.Position
import models.SimpleQube
import kotlin.math.floor

enum class Align{
    LEFT,RIGHT,CENTER
}

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
    fun updateText(newtext:String,cols:Int,align:Align=Align.LEFT){
        occupies.clear()
        chars.clear()
        var col:Int = (super.position.x/BaseUnits.ONE).toInt()
        var row:Int = (super.position.y/BaseUnits.ONE).toInt()

        for(line in newtext.split('\n')){//split by custom new line
            for(lrow in line.split(".{$cols}")){//line wrap
                var lfill:Int = 0
                for(chr:Char in lrow){
                    var delta:Int = 0
                    if(align == Align.RIGHT){
                        delta = cols - line.length
                    }else if(align == Align.CENTER){
                        delta = floor((cols - line.length).toFloat()/2.0).toInt()
                    }
                    if(delta > 0){
                        lfill=delta
                    }
                    val chrpos:Position = Position(col+lfill,row,0)
                    occupies.add(SimpleQube(chrpos,1,1,0))
                    chars.put(chrpos,chr)
                    ++col
                }
                ++row
                col = (super.position.x/BaseUnits.ONE).toInt()
            }
            ++row
            col = (super.position.x/BaseUnits.ONE).toInt()
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