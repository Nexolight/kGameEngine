package abstracted

import flow.ActionHandler
import flow.NotifyThread
import models.Field
import models.Notification
import models.NotificationType
import mu.KLogger
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

abstract class LogicCompositor(ah:ActionHandler) : NotifyThread(){
    lateinit var log: KLogger
    private var kill = false
    private val ah:ActionHandler = ah
    private val uics:ConcurrentLinkedDeque<UICompositor> = ConcurrentLinkedDeque<UICompositor>()
    private val pendingUics:ConcurrentLinkedQueue<UICompositor> = ConcurrentLinkedQueue<UICompositor>()
    abstract var field:Field

    override fun run(){
        log = KotlinLogging.logger(this::class.java.name)
        log.info { "LogicCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE))
        while(!kill){
            while(pendingUics.isNotEmpty()){//A consumer registered
                uics.add(pendingUics.poll())
            }
            if(uics.isEmpty()){//No consumers available
                Thread.sleep(16)
                continue
            }
            requestAction()
            for(uic in uics){
                if(uic.isAlive){
                    uic.onLCReady(field)
                }

            }
            Thread.sleep(16)
        }
        log.info { "LogicCompositor stopped gracefully!" }
    }

    /**
     * Calls the individual game logic to perform the
     * next action
     */
    abstract fun requestAction()

    override fun onNotify(n: Notification) {

        /**
         * We should multiplex to all registered ui compositors
         * if they notify us
         */
        if(n.type == NotificationType.UI_COMPOSITOR_AVAILABLE && n.uic != null){
            if(n.uic in uics){
                log.warn { "UI compositor already noticed" }
                return
            }
            log.info { "New UICompositor noticed" }
            pendingUics.add(n.uic)
            return
        }
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            log.info { "Killing LogicCompositor!" }
            kill = true
            return
        }
    }
}