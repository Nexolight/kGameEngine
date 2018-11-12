package models

import java.util.*


/**
 * Class that defines the game area including everything
 * that is on it
 */
class Field(){
    var width:Int = 50
    var height:Int = 30
    private val snake: Snake = Snake()
    private val buffs:Deque<Buff> = LinkedList<Buff>()
    fun Field(){

    }

    /**
     * Set the snake back to it's default position
     */
    fun resetSnake(){
        snake.reset()
    }

    /**
     * Clear all buffs
     */
    fun resetBuffs(){
        buffs.clear()
    }

    /**
     * Reset everything on the field
     * to it's default position
     */
    fun resetAll(){
        this.resetSnake()
        this.resetBuffs()
    }
}