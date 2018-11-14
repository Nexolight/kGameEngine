package models

import abstract.Entity
import java.util.*
import javax.swing.border.Border

enum class BorderType(){
    REPEAT,
    BLOCK
}

/**
 * Class that defines the game area including everything
 * that is on it
 */
class Field(){
    var width:Int = 50
    var height:Int = 30
    var borderType = BorderType.REPEAT
    private val entities:Deque<Entity> = LinkedList<Entity>()

    fun Field(width:Int,height:Int,borderType:BorderType){
        this.width=width
        this.height=height
        this.borderType=borderType
    }

    /**
     * Resets the field to it's original state
     */
    fun reset(){
        entities.clear()
    }


}