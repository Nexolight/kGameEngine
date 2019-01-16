package games.snake.entitylogic

import abstracted.logic.EntityLogic
import flow.ActionHandler
import flow.NotifyThread
import games.snake.entitylogic.entities.SnakeEntity
import models.Notification
import models.NotificationType
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The player logic
 * Applies user inputs to the SnakeEntity
 */
class PlayerLogic(var snake:SnakeEntity, val ah:ActionHandler): EntityLogic(){
    var kill:Boolean = false
    var lastInput:Char = ' '

    override fun run(){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Player Spawned!"))
        while(!kill){
            if(super.actionRequestPending()){
                //TODO:snake logic
                super.actionRequestDone()
            }else{
                /**
                 * Lower value = faster response to action request
                 */
                Thread.sleep(16)
            }
        }
    }

    override fun onNotify(n: Notification) {

        //Store user input
        if(n.type == NotificationType.USERINPUT){
            lastInput = n.chr
        }

        //Kill on termination
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Killing PlayerLogic!"))
            kill = true
            return
        }
    }
}