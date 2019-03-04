package abstracted.entity.presets

import abstracted.entity.StaticEntity
import abstracted.ui.`if`.ASCIISupport
import models.*
import java.util.TreeSet
import kotlin.math.abs


/**
 * Extended text entity that can display value pairs on the field
 * in a separate box.
 */
class TextPairEntity : StaticEntity, ASCIISupport {

    private var pairs:HashMap<Int,TextPair> = HashMap<Int,TextPair>()
    private var columns:Int = 20
    private var border:Char? = null
    private var borderPadding = 1

    private var occupies:MutableList<AdvancedQube> = ArrayList<AdvancedQube>()
    private var chars:HashMap<Position,Char> = HashMap<Position,Char>()
    private var offcol:Int = (super.position.x/ BaseUnits.ONE).toInt()
    private var offrow:Int = (super.position.y/ BaseUnits.ONE).toInt()

    constructor():super(){}
    constructor(pos: Position,
                rotation: Rotation = Rotation(0.0,0.0,0.0),
                columns:Int = 20,
                borderPadding:Int = 1,
                border:Char? = null) : super(pos,rotation){
        this.columns=columns
        this.border=border
        if(border != null){
            this.columns-=2+borderPadding*2
        }
    }


    override fun getOccupyRepresentation(pos: Position, rota: Rotation): Char {
        val chr:Char? = chars.get(pos)

        if(chr != null){
            return chr
        }
        return ' '
    }

    override fun occupies(): List<AdvancedQube> {
        return occupies
    }

    /**
     * Add or update an existing key/value set.
     */
    fun setPair(row:Int, name:String,value:String,update:Boolean = false){
        pairs.put(row,TextPair(name,value))
        if(update){updatePairs()}
    }

    /**
     * Remove a pair if it exists.
     */
    fun removePair(row:Int,update:Boolean = false){
        pairs.remove(row)
        if(update){updatePairs()}
    }

    fun updatePairsSub(pair:TextPair,offcol:Int,offrow:Int,textRow:Int,tbBorder:Boolean=false){
        var constr:String=""
        var textCol:Int = 0
        var delta:Int = columns - pair.first.length - pair.second.length

        if(tbBorder){
            if(tbBorder){
                constr = border.toString().repeat(columns)
            }else{
                delta-=borderPadding*2+2
                constr = buildStr(border.toString()+" ".repeat(borderPadding)+pair.first,pair.second+" ".repeat(borderPadding)+border.toString(),delta)
            }
        }else{
            constr = buildStr(pair.first,pair.second,delta)
        }


        val effectiveRow=offrow+textRow
        for(chr:Char in constr){
            val effectiveCol=offcol+textCol
            val pos:Position = Position(effectiveCol,effectiveRow,0)
            occupies.add(AdvancedQube(pos,
                    Size(1,1,0).makeFlat(),
                    Rotation(0.0,0.0,0.0)))
            chars.put(pos,chr)
            textCol++
        }
    }

    /**
     * Updates the character in the entity after the pair list was changed
     */
    fun updatePairs(){
        occupies.clear()
        chars.clear()

        offcol = (super.position.x/ BaseUnits.ONE).toInt()
        offrow = (super.position.y/ BaseUnits.ONE).toInt()
        var textRow:Int = 0
        if(border != null){
            updatePairsSub(TextPair(border.toString().repeat(columns)),offcol,offrow,textRow,true)
            textRow++
            for(padding in 0 until borderPadding){
                updatePairsSub(TextPair("",""),offcol,offrow,textRow)
                textRow++
            }
        }

        var indexRow:Int = 0
        for (pairRow in 0 .. TreeSet(pairs.keys).last()) {
            var pair:TextPair? = pairs.get(pairRow)
            if(pair == null){
                pair = TextPair("","")
            }
            updatePairsSub(pair,offcol,offrow,textRow+pairRow)
            indexRow++
        }
        textRow+=indexRow
        if(border != null){
            for(padding in 0 until borderPadding){
                updatePairsSub(TextPair("",""),offcol,offrow,textRow)
                textRow++
            }
            updatePairsSub(TextPair(border.toString().repeat(columns)),offcol,offrow,textRow,true)
            textRow++
        }

        /*
        var str:String = ""
        var emptyLine:String = "\n"

        if(border != null){
            emptyLine=border.toString()+" ".repeat(borderPadding*2)+" ".repeat(columns)+border.toString()+"\n"
            str+=border.toString().repeat(columns+borderPadding*2+2)+"\n"
            str+=emptyLine.repeat(borderPadding)
        }

        for (row in 0 .. TreeSet(pairs.keys).last()) {
            val pair:TextPair? = pairs.get(row)
            if(pair == null){
                str+="\n"
                continue
            }
            
            if(border != null){
                val space:Int = columns - pair.first.length - pair.second.length
                str+=border.toString()+" ".repeat(borderPadding)+limitStr(pair.first,pair.second,space)+" ".repeat(borderPadding)+border.toString()+"\n"
            }else{
                val space:Int = columns - pair.first.length - pair.second.length
                str+=limitStr(pair.first,pair.second,space)+"\n"
            }

        }

        if(border != null){
            str+=emptyLine.repeat(borderPadding)
            str+=border.toString().repeat(columns+borderPadding*2+2)+"\n"
        }

        super.updateText(str,columns,Align.LEFT)*/
    }


    /**
     * Cutoff the string if the delta is negative,
     */
    private fun buildStr(inStr1:String,inStr2:String,delta:Int):String{
        if(delta >= 0){
            return inStr1+" ".repeat(delta)+inStr2
        }
        if(inStr1.length-4 >= delta){
            return inStr1.substring(0,inStr1.length-4-abs(delta))+"... "+inStr2
        }
        return "Err"+" ".repeat(columns-3)//too long
    }
}

class TextPair(val first:String = "", val second:String = ""){}
