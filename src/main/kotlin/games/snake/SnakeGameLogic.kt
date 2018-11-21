package games.snake

import abstracted.LogicCompositor
import abstracted.entity.StaticEntity
import flow.ActionHandler
import games.snake.models.WallEntity
import models.BaseUnits
import models.BorderType
import models.Field
import models.Position

class SnakeGameLogic(ah:ActionHandler) : LogicCompositor(ah){
    override var field: Field = Field(SnakeParams.mapwidth,SnakeParams.mapheight, BorderType.BLOCK)

    override fun requestStart() {
        genMap()
    }

    override fun requestAction() {
        //
    }

    fun genMap(){
        field.entities.apply {
            WallEntity(Position(0,0,0),SnakeParams.mapwidth,1)
        }
        field.entities.apply {
            WallEntity(Position(0,SnakeParams.mapheight,0),SnakeParams.mapwidth,1)
        }
        field.entities.apply {
            WallEntity(Position(0,1,0),1,SnakeParams.mapheight-2)
        }
        field.entities.apply {
            WallEntity(Position(SnakeParams.mapwidth,1,0),1,SnakeParams.mapheight-2)
        }
    }

}