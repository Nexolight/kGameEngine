package abstracted.entity.presets

import models.Position
import models.Rotation
import java.util.TreeSet
import kotlin.math.abs


/**
 * Extended text entity that can display value pairs on the field
 * in a separate box.
 */
class TextPairEntity: TextEntity {

    private var pairs:HashMap<Int,TextPair> = HashMap<Int,TextPair>()
    private var columns:Int = 20
    private var border:Char? = null
    private var borderPadding = 1


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

    /**
     * Updates the character in the entity after the pair list was changed
     */
    fun updatePairs(){
        var str:String = ""
        var emptyLine:String = "\n "

        if(border != null){
            emptyLine=border.toString()+" ".repeat(borderPadding*2)+" ".repeat(columns)+border.toString()+"\n"
            str+=border.toString().repeat(columns+borderPadding*2+2)+"\n"
            str+=emptyLine.repeat(borderPadding)
        }

        for (row in 0 .. TreeSet(pairs.keys).last()) {
            val pair:TextPair? = pairs.get(row)
            if(pair == null){
                str+="\n "
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

        super.updateText(str,columns,Align.LEFT)
    }


    /**
     * Cutoff the string if the delta is negative,
     */
    private fun limitStr(inStr1:String,inStr2:String,delta:Int):String{
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
