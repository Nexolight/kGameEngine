package models

import abstracted.Entity
import com.google.common.collect.Sets
import java.util.*
import java.util.concurrent.ConcurrentMap

enum class BorderType(){
    REPEAT,
    BLOCK
}

/**
 * Class that defines the game area including everything
 * that is on it
 */
class Field{
    var width:Double
    var height:Double
    var borderType = BorderType.REPEAT
    val entities:Set<Entity> = Sets.newConcurrentHashSet()

    constructor(width:Int = 1000,height:Int = 1000,borderType:BorderType){
        this.width=width*BaseUnits.ONE
        this.height=height*BaseUnits.ONE
        this.borderType=borderType
    }

}