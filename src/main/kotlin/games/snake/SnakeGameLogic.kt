package games.snake

import abstracted.LogicCompositor
import flow.ActionHandler
import abstracted.entity.presets.Align
import abstracted.entity.presets.TextEntity
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.Pool
import games.snake.entitylogic.PlayerLogic
import games.snake.entitylogic.entities.SnakeEntity
import games.snake.entitylogic.entities.WallEntity
import models.*

class SnakeGameLogic : LogicCompositor{

    var firstAction:Boolean = true
    val welcome: TextEntity = TextEntity(Position(2, 10, 0))
    override var field: Field = Field(SnakeDefaultParams.mapwidth,SnakeDefaultParams.mapheight)
    var player:PlayerLogic? = null

    constructor(ah:ActionHandler,kryoPool: Pool<Kryo>):super(ah,kryoPool){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
    }

    override fun requestAction() {
        val elements = field.entities.size

        //TODO: ingame menu debug log options
        if(super.actionRequestTicks % 6 == 0.toLong()){
            igLogI("ActionRequests: ${super.actionRequestTicks} elements: $elements")
        }


        /**
         * Welcome sequence
         */
        if(firstAction){
            if(super.actionRequestTicks == 0.toLong()){
                super.igLogI("Loading game")
                genMap()
                field.entities.add(welcome)
            }
            if(super.actionRequestTicks < SnakeDefaultParams.welcomeTimer){
                super.addActionRequestDelay(1000)
                welcome.updateText(
                        "${SnakeDefaultParams.welcomeMsg}\n${5-super.actionRequestTicks}",
                        SnakeDefaultParams.mapwidth-4,
                        Align.CENTER)
            }else{
                field.entities.remove(welcome)
                genSnake()
                firstAction = false
            }
        }else{
            //TODO: ingame menu speed option
            super.addActionRequestDelay(SnakeDefaultParams.tickSpeed)
        }


        if(player != null){
            player?.actionRequest()
            /**
             * TODO:
             * at this point updates need to be finished
             * otherwise it will mess up the serialization
             */
            while(player?.actionRequestPending() == true){
                Thread.sleep(1)
            }
        }

    }

    /**
     * Snake generation
     */
    fun genSnake(){
        val newPlayer:PlayerLogic = PlayerLogic(
                SnakeEntity(
                        Position(SnakeDefaultParams.mapwidth/2,SnakeDefaultParams.mapheight/2,0),
                        Rotation(0.0,0.0,0.0)
        ), ah)
        field.entities.add(newPlayer.snake)
        newPlayer.start()
        player = newPlayer
    }

    /**
     * Map generation
     */
    fun genMap(){
        field.entities.add(
            WallEntity(
                    Position(0,0,0),
                    Size(SnakeDefaultParams.mapwidth,1,0),
                    Rotation(0.0,0.0,0.0))
        )
        field.entities.add(
            WallEntity(
                    Position(0,SnakeDefaultParams.mapheight-1,0),
                    Size(SnakeDefaultParams.mapwidth,1,0),
                    Rotation(0.0,0.0,0.0))
        )
        field.entities.add(
            WallEntity(
                    Position(0,1,0),
                    Size(1,SnakeDefaultParams.mapheight-2,0),
                    Rotation(0.0,0.0,0.0))
        )
        field.entities.add(
            WallEntity(
                    Position(SnakeDefaultParams.mapwidth-1,1,0),
                    Size(1,SnakeDefaultParams.mapheight-2,0),
                    Rotation(0.0,0.0,0.0))
        )
    }

    override fun onLCNotify(n: Notification) {
        //DEBUG only so far
        if(n.type == NotificationType.USERINPUT){
            igLogI("User input: ${n.chr}")
            return
        }
    }

}