package models

import abstracted.Entity
import com.esotericsoftware.kryo.Kryo
import com.google.common.collect.Sets
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentMap

/**
 * Class that defines the game area including everything
 * that is on it
 */
class Field(){
    var width:Double = 0.0
    var height:Double = 0.0
    val entities:ConcurrentLinkedDeque<Entity> = ConcurrentLinkedDeque<Entity>()

    constructor(width:Int = 1000,height:Int = 1000):this(){
        this.width=width*BaseUnits.ONE
        this.height=height*BaseUnits.ONE
    }
}