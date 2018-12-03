package models

import abstracted.Entity
import com.google.common.collect.Sets
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentMap

/**
 * Class that defines the game area including everything
 * that is on it
 */
class Field{
    var width:Double
    var height:Double
    val entities:ConcurrentLinkedDeque<Entity> = ConcurrentLinkedDeque<Entity>()

    constructor(width:Int = 1000,height:Int = 1000){
        this.width=width*BaseUnits.ONE
        this.height=height*BaseUnits.ONE
    }

}