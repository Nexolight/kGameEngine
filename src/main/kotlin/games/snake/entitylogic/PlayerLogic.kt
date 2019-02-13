package games.snake.entitylogic

import abstracted.Entity
import abstracted.logic.EntityLogic
import flow.ActionHandler
import flow.NotifyThread
import games.snake.SnakeDefaultParams
import games.snake.entitylogic.entities.SnakeEntity
import games.snake.entitylogic.entities.WallEntity
import models.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The player logic
 * Applies user inputs to the SnakeEntity
 */
class PlayerLogic(var snake:SnakeEntity, val ah:ActionHandler): EntityLogic() {

    var kill:Boolean = false
    var moveTo:Char = SnakeDefaultParams.ctrlLEFT
    var teleport = false
    var commitedMove = ' '
    var initialFeed = SnakeDefaultParams.initialFeed
    private val notifyQueue: ConcurrentLinkedQueue<Notification> = ConcurrentLinkedQueue<Notification>()


    override fun run(){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.COLLISION))
        ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Player Spawned!"))

        //snake.setCollisionHandler(this)

        while(!kill){
            if(super.actionRequestPending()){
                processNotifications()
                if(initialFeed > 0){
                    snake.feed()
                    initialFeed--
                }

                //TODO: check collision

                //TODO: check & apply for food

                //TODO: check & apply buffs

                //TODO: Key rebinding via broadcast and menu

                /**
                 * Player movement
                 */
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

                /**
                 * Teleporting from wall to wall
                 */
                if(teleport){
                    if((snake.position.x/BaseUnits.ONE).toInt() >= SnakeDefaultParams.mapwidth){
                        snake.transform(
                                Position(
                                        (snake.position.x/BaseUnits.ONE).toInt()*-1+1,
                                        0,
                                        0
                                )
                        )
                    }else if((snake.position.x/BaseUnits.ONE).toInt() <= 1){
                        snake.transform(
                                Position(
                                        SnakeDefaultParams.mapwidth-1,
                                        0,
                                        0
                                )
                        )
                    }else if((snake.position.y/BaseUnits.ONE).toInt() >= SnakeDefaultParams.mapheight){
                        snake.transform(
                                Position(
                                        0,
                                        (snake.position.y/BaseUnits.ONE).toInt()*-1+1,
                                        0
                                )
                        )
                    }else if((snake.position.y/BaseUnits.ONE).toInt() <= 0){
                        snake.transform(
                                Position(
                                        0,
                                        SnakeDefaultParams.mapheight-1,
                                        0
                                )
                        )
                    }
                    teleport = false
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

    fun processNotifications(){
        while(notifyQueue.size>0){
            val n:Notification = notifyQueue.poll()
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
                return
            }

            if(n.type == NotificationType.COLLISION && n.collision != null && n.collision.collidingSrc.equals(snake)){
                if(n.collision.collidingDst is WallEntity){
                    ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Player teleport"))
                    teleport=true
                    //snake.feed()
                }

                //TODO: food

                return
            }

            //Kill on termination
            if(n.type == NotificationType.SIGNAL && n.n == 2){
                ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Killing PlayerLogic!"))
                kill = true
                return
            }
        }
    }

    override fun onNotify(n: Notification) {
        notifyQueue.add(n) //use the player logic thread later for processing
    }
}