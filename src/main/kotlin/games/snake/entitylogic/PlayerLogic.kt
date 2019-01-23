package games.snake.entitylogic

import abstracted.logic.EntityLogic
import flow.ActionHandler
import flow.NotifyThread
import games.snake.SnakeDefaultParams
import games.snake.entitylogic.entities.SnakeEntity
import models.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The player logic
 * Applies user inputs to the SnakeEntity
 */
class PlayerLogic(var snake:SnakeEntity, val ah:ActionHandler): EntityLogic(){
    var kill:Boolean = false
    var moveTo:Char = 'a' //left by default
    var commitedMove = ' '
    var initialFeed = SnakeDefaultParams.initialFeed

    override fun run(){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Player Spawned!"))
        while(!kill){
            if(super.actionRequestPending()){
                if(initialFeed > 0){
                    snake.feed()
                    initialFeed--
                }

                //TODO: check collision

                //TODO: check & apply for food

                //TODO: check & apply buffs

                //TODO: Key rebinding via broadcast and menu
                when(moveTo){
                    SnakeDefaultParams.ctrlFWD -> {
                        snake.transform(Position(0,-1,0))
                        snake.transform(Rotation(
                                snake.getRotationValues().xAxis*-1,
                                0.0,
                                0.0)
                        )
                        commitedMove=moveTo
                    }
                    SnakeDefaultParams.ctrlLEFT -> {
                        snake.transform(Position(-1,0,0))
                        snake.transform(
                                Rotation(
                                        (snake.getRotationValues().xAxis*-1)-90,
                                        0.0,
                                        0.0)
                        )
                        commitedMove=moveTo
                    }
                    SnakeDefaultParams.ctrlBWD -> {
                        snake.transform(Position(0,1,0))
                        snake.transform(
                                Rotation(
                                        (snake.getRotationValues().xAxis*-1)+180,
                                        0.0,
                                        0.0)
                        )
                        commitedMove=moveTo
                    }
                    SnakeDefaultParams.ctrlRIGHT -> {
                        snake.transform(Position(1,0,0))
                        snake.transform(
                                Rotation(
                                        (snake.getRotationValues().xAxis*-1)+90,
                                        0.0,
                                        0.0)
                        )
                        commitedMove=moveTo
                    }
                }
                //for(bpart:AdvancedQube in snake.body.iterator()){
                //    ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,bpart.toString()))
                //}
                super.actionRequestDone()
            }else{
                Thread.sleep(1)
            }
        }
    }

    override fun onNotify(n: Notification) {

        //Store user input
        if(n.type == NotificationType.USERINPUT){
            val inp:Char = n.chr.toLowerCase()

            //Ignore same key twice
            if(commitedMove == inp){
                return
            }

            //movement filter
            if(inp !in Arrays.asList(
                SnakeDefaultParams.ctrlFWD,
                SnakeDefaultParams.ctrlLEFT,
                SnakeDefaultParams.ctrlBWD,
                SnakeDefaultParams.ctrlRIGHT)
            ){
                return
            }

            //disallow player to move into himself
            if(
                    (inp == SnakeDefaultParams.ctrlFWD && commitedMove == SnakeDefaultParams.ctrlBWD) ||
                    (inp == SnakeDefaultParams.ctrlLEFT && commitedMove == SnakeDefaultParams.ctrlRIGHT) ||
                    (inp == SnakeDefaultParams.ctrlBWD && commitedMove == SnakeDefaultParams.ctrlFWD) ||
                    (inp == SnakeDefaultParams.ctrlRIGHT && commitedMove == SnakeDefaultParams.ctrlLEFT)
            ){
                return
            }
            moveTo = inp
        }

        //Kill on termination
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Killing PlayerLogic!"))
            kill = true
            return
        }
    }
}