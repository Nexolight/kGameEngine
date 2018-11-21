package abstracted

import flow.ActionHandler
import flow.NotifyThread
import models.Field
import models.Notification
import models.NotificationType
import mu.KLogger
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

abstract class LogicCompositor(ah:ActionHandler) : NotifyThread(){
    lateinit var log: KLogger
    private var kill = false
    private val ah:ActionHandler = ah
    private val uicLock = ReentrantReadWriteLock()
    private val uics:Deque<UICompositor> = LinkedList<UICompositor>()
    abstract var field:Field

    override fun run(){
        log = KotlinLogging.logger(this::class.java.name)
        log.info { "LogicCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE))
        requestStart()
        while(!kill){
            requestAction()
            uicLock.write {
                for(uic in uics){
                    if(uic.isAlive){
                        //uic.onLCReady()//send work
                        uic.field(field)
                    }

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

    /**
     * Calls the individual game logic to perform the
     * first action
     */
    abstract fun requestStart()

    override fun onNotify(n: Notification) {

        /**
         * We should multiplex to all registered ui compositors
         * if they notify us
         */
        if(n.type == NotificationType.UI_COMPOSITOR_AVAILABLE && n.uic != null){
            uicLock.write {
                if(n.uic in uics){
                    log.warn { "UI compositor already noticed" }
                    return
                }
                log.info { "New UICompositor noticed" }
                uics.add(n.uic)
                return
            }
        }
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            log.info { "Killing LogicCompositor!" }
            kill = true
            return
        }
    }
}