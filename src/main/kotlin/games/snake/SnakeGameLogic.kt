package games.snake

import abstracted.LogicCompositor
import flow.ActionHandler
import games.snake.entitylogic.entities.Align
import games.snake.entitylogic.entities.TextEntity
import games.snake.entitylogic.entities.WallEntity
import models.*

class SnakeGameLogic : LogicCompositor{

    var firstAction:Boolean = true
    val welcome:TextEntity = TextEntity(Position(5,10,0))
    override var field: Field = Field(SnakeParams.mapwidth,SnakeParams.mapheight)

    constructor(ah:ActionHandler):super(ah){
    }


    override fun requestAction() {
        val elements = field.entities.size
        super.igLogI("frames: ${super.renderTicks}, ARs: ${super.actionRequestTicks} elements: $elements")

        /**
         * Welcome sequence
         */
        if(firstAction){
            if(super.actionRequestTicks == 0.toLong()){
                super.igLogI("Loading game")
                super.framecap = 100//don't need more
                genMap()
                field.entities.add(welcome)
            }
            if(super.actionRequestTicks < 5){
                welcome.updateText(
                        "${SnakeParams.welcomeMsg}\n${5-super.actionRequestTicks}",
                        SnakeParams.mapwidth-4,
                        Align.CENTER)
            }else{
                field.entities.remove(welcome)
                firstAction = false
                super.framecap = 16
            }
        }

        //don't need more here.
        super.addActionRequestDelay(1000)
    }

    /**
     * Snake generation
     */
    fun genSnake(){
        /*
        field.entities.add(
                SnakeEntity(
                        Position(0,0,0),

                )
        )*/
    }

    /**
     * Map generation
     */
    fun genMap(){
        field.entities.add(
            WallEntity(Position(0,0,0),SnakeParams.mapwidth,1))
        field.entities.add(
            WallEntity(Position(0,SnakeParams.mapheight-1,0),SnakeParams.mapwidth,1)
        )
        field.entities.add(
            WallEntity(Position(0,1,0),1,SnakeParams.mapheight-2)
        )
        field.entities.add(
            WallEntity(Position(SnakeParams.mapwidth-1,1,0),1,SnakeParams.mapheight-2)
        )
    }

}