package games.snake

import abstracted.LogicCompositor
import flow.ActionHandler
import games.snake.models.WallEntity
import models.*

class SnakeGameLogic : LogicCompositor{

    var firstAction:Boolean = true

    constructor(ah:ActionHandler):super(ah){
    }

    override var field: Field = Field(SnakeParams.mapwidth,SnakeParams.mapheight, BorderType.BLOCK)

    override fun requestAction() {
        if(firstAction == true){
            genMap()
            firstAction = false
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