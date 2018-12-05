package games.snake

import abstracted.LogicCompositor
import flow.ActionHandler
import games.snake.models.Align
import games.snake.models.TextEntity
import games.snake.models.WallEntity
import models.*

class SnakeGameLogic : LogicCompositor{

    var firstAction:Boolean = true
    val welcome:TextEntity = TextEntity(Position(5,10,0))
    override var field: Field = Field(SnakeParams.mapwidth,SnakeParams.mapheight)


    constructor(ah:ActionHandler):super(ah){
    }


    override fun requestAction() {
        if(firstAction){
            genMap()
            if(super.ticks == 0.toLong()){
                super.igLogI("Loading game")
            }
            field.entities.add(welcome)
            if(super.ticks < 3){
                welcome.updateText(
                        "${SnakeParams.welcomeMsg}\n${3-super.ticks}",
                        SnakeParams.mapwidth-4,
                        Align.CENTER)
                super.addActionRequestDelay(1000)
            }else{
                field.entities.remove(welcome)//TODO:this isn't removed but should be
                firstAction = false
            }
        }
    }

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