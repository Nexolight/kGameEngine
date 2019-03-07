package games.snake.entitylogic

import abstracted.Entity
import abstracted.logic.EntityLogic
import flow.ActionHandler
import flow.NotifyThread
import games.snake.SnakeBuffs
import games.snake.SnakeDefaultParams
import games.snake.SnakeGameSignals
import games.snake.entitylogic.entities.EdibleEntity
import games.snake.entitylogic.entities.SnakeEntity
import games.snake.entitylogic.entities.WallEntity
import models.*
import physics.CollisionHelpers
import physics.`if`.RigidBody
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The player logic
 * Applies user inputs to the SnakeEntity
 */
class PlayerLogic(val field:Field, var snake:SnakeEntity, val ah:ActionHandler): EntityLogic() {
    var kill:Boolean = false
    var death:Boolean = false
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
            if(super.actionRequestPending() && death){
                super.actionRequestDone()
                Thread.sleep(1)
            }
            if(super.actionRequestPending()){//process the last Action request
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
                                0.0,
                                0.0,
                                snake.getRotationValues().zAxis*-1)
                        )
                        commitedMove=moveTo
                    }
                    SnakeDefaultParams.ctrlLEFT -> {
                        snake.transform(Position(-1,0,0))
                        snake.transform(
                                Rotation(
                                        0.0,
                                        0.0,
                                        (snake.getRotationValues().zAxis*-1)-90)
                        )
                        commitedMove=moveTo
                    }
                    SnakeDefaultParams.ctrlBWD -> {
                        snake.transform(Position(0,1,0))
                        snake.transform(
                                Rotation(
                                        0.0,
                                        0.0,
                                        (snake.getRotationValues().zAxis*-1)+180)
                        )
                        commitedMove=moveTo
                    }
                    SnakeDefaultParams.ctrlRIGHT -> {
                        snake.transform(Position(1,0,0))
                        snake.transform(
                                Rotation(
                                        0.0,
                                        0.0,
                                        (snake.getRotationValues().zAxis*-1)+90)
                        )
                        commitedMove=moveTo
                    }
                }

                /**
                 * Teleporting from wall to wall
                 */
                if(teleport){
                    if((snake.position.x/BaseUnits.ONE).toInt() >= SnakeDefaultParams.mapwidth &&
                            commitedMove == SnakeDefaultParams.ctrlRIGHT){
                        snake.transform(
                                Position(
                                        (snake.position.x/BaseUnits.ONE).toInt()*-1+1,
                                        0,
                                        0
                                )
                        )
                    }else if((snake.position.x/BaseUnits.ONE).toInt() <= 1 &&
                            commitedMove == SnakeDefaultParams.ctrlLEFT){
                        snake.transform(
                                Position(
                                        SnakeDefaultParams.mapwidth-1,
                                        0,
                                        0
                                )
                        )
                    }else if((snake.position.y/BaseUnits.ONE).toInt() >= SnakeDefaultParams.mapheight &&
                            commitedMove == SnakeDefaultParams.ctrlBWD){
                        snake.transform(
                                Position(
                                        0,
                                        (snake.position.y/BaseUnits.ONE).toInt()*-1+1,
                                        0
                                )
                        )
                    }else if((snake.position.y/BaseUnits.ONE).toInt() <= 0 &&
                            commitedMove == SnakeDefaultParams.ctrlFWD){
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

        //clear AR before exit
        if(super.actionRequestPending()){
            super.actionRequestDone()
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
                    teleport=true
                    ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Player teleport"))
                    //snake.feed()
                }else if(n.collision.collidingDst is EdibleEntity){
                    val edible:EdibleEntity = n.collision.collidingDst
                    applyEdible(edible)
                }else if(n.collision.collidingDst is SnakeEntity){
                    death=true
                    ah.notify(Notification(this,NotificationType.GAMESIGNAL, NotifyPair(SnakeGameSignals.playerDeath, 0)))
                    ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Player dead"))
                }

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

    /**
     * The snake just ate something
     * let's apply the buffs and remove that piece
     * from the field
     */
    fun applyEdible(edible:EdibleEntity){
        for(buff:Buff in edible.buffs){

            if(buff == SnakeBuffs.food){
                snake.feed()
                ah.notify(Notification(this,NotificationType.GAMESIGNAL, NotifyPair(SnakeGameSignals.snakeEat, 1)))
            }else if(buff == SnakeBuffs.speeddownM){
                ah.notify(Notification(this,NotificationType.GAMESIGNAL, NotifyPair(SnakeGameSignals.newBuffValue, SnakeBuffs.speeddownM.buffValue)))
            }else if(buff == SnakeBuffs.speeddownL){
                ah.notify(Notification(this,NotificationType.GAMESIGNAL, NotifyPair(SnakeGameSignals.newBuffValue, SnakeBuffs.speeddownL.buffValue)))
            }else if(buff == SnakeBuffs.speedupM){
                ah.notify(Notification(this,NotificationType.GAMESIGNAL, NotifyPair(SnakeGameSignals.newBuffValue, SnakeBuffs.speedupM.buffValue)))
            }else if(buff == SnakeBuffs.speedupL){
                ah.notify(Notification(this,NotificationType.GAMESIGNAL, NotifyPair(SnakeGameSignals.newBuffValue, SnakeBuffs.speedupL.buffValue)))
            }
        }
        for(fE:Entity in field.entities){
            if(fE is EdibleEntity && fE.equals(edible)){
                fE.wasEaten=true
            }
        }
        if(field.entities.contains(edible)){//might be artificial
            field.entities.remove(edible)
        }
    }



    override fun onNotify(n: Notification) {
        notifyQueue.add(n) //use the player logic thread later for processing
    }
}