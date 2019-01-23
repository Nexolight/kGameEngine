package abstracted.entity.presets

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import kotlin.math.floor

enum class Align{
    LEFT,RIGHT,CENTER
}

/**
 * This entity can be used to display text on the field
 * TODO: add 3d support
 *
 */
class TextEntity : StaticEntity, ASCIISupport{

    constructor():super(){}
    constructor(pos: Position, rotation:Rotation = Rotation(0.0,0.0,0.0)) : super(pos,rotation){}

    private var text:String = ""
    private var cols:Int = 0
    private var occupies:MutableList<AdvancedQube> = ArrayList<AdvancedQube>()
    private var chars:HashMap<Position,Char> = HashMap<Position,Char>()


    /**
     * Update the text in this entity
     * newtext: The new text to show
     * cols:    The max width (linewrap)
     * align:   The alignment
     */
    fun updateText(newtext:String,cols:Int,align: Align = Align.LEFT){
        occupies.clear()
        chars.clear()
        var col:Int = (super.position.x/ BaseUnits.ONE).toInt()
        var row:Int = (super.position.y/ BaseUnits.ONE).toInt()

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
                    val chrpos: Position = Position(col + lfill, row, 0)

                    occupies.add(AdvancedQube(
                            chrpos,
                            Size(1, 1, 0).makeFlat(),
                            Rotation(0.0,0.0,0.0)))
                    chars.put(chrpos,chr)
                    ++col
                }
                ++row
                col = (super.position.x/ BaseUnits.ONE).toInt()
            }
            ++row
            col = (super.position.x/ BaseUnits.ONE).toInt()
        }
    }

    override fun occupies(): List<AdvancedQube> {
        return occupies
    }

    override fun getOccupyRepresentation(pos: Position, rota:Rotation): Char {
        val chr:Char? = chars.get(pos)
        if(chr != null){
            return chr
        }
        return ' '
    }


}