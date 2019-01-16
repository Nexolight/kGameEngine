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
import java.util.concurrent.CopyOnWriteArrayList

abstract class LogicCompositor(ah:ActionHandler) : NotifyThread(){
    companion object {
        val maxLogLen:Int = 5//TODO: parameter
        val defaultFrameCap:Long = 16
    }

    lateinit var log: KLogger
    private var kill = false
    private val ah:ActionHandler = ah
    private val uics:ConcurrentLinkedDeque<UICompositor> = ConcurrentLinkedDeque<UICompositor>()
    private val pendingUics:ConcurrentLinkedQueue<UICompositor> = ConcurrentLinkedQueue<UICompositor>()
    abstract var field:Field

    /**
     * Either we deepcopy the list we send to the UIC or
     * we use a CopyOnWriteArrayList which is expensive
     * but acceptable for this purpose.
     */
    private val iglog:CopyOnWriteArrayList<String> = CopyOnWriteArrayList<String>()

    private var renderDelay:Long = 0
    private var actionRequestDelay:Long = 0
    var renderTicks:Long = 0
        private set
    var actionRequestTicks:Long = 0
        private set

    /**
     * Controls the time delay between each frame
     */
    var framecap:Long = defaultFrameCap//TODO: ingame setting?


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
                Thread.sleep(framecap)
                continue
            }

            if(actionRequestDelay > 0){
                actionRequestDelay-=framecap
            }else{
                actionRequestDelay = 0
                requestAction()
                ++actionRequestTicks
            }

            for(uic in uics){
                if(uic.isAlive){
                    uic.onLCReady(field)
                    uic.onLCReady(iglog)
                }

            }
            ++renderTicks
            if(renderDelay > 0){
                Thread.sleep(renderDelay)
                renderDelay = 0
            }else{//TODO: this is a framecap - should be a setting
                Thread.sleep(framecap)
            }
        }
        log.info { "LogicCompositor stopped gracefully!" }
    }

    /**
     * Add a log line to be drawed ingame
     */
    fun igLog(str:String,prefix:String){
        iglog.add("$prefix$str")
        if(iglog.size > LogicCompositor.maxLogLen){
            iglog.removeAt(0)
        }
    }

    /**
     * Add a info log line to be drawed ingame
     */
    fun igLogI(str:String){
        igLog(str,"[INFO]  - ")
    }

    /**
     * Add a warn log line to be drawed ingame
     */
    fun igLogW(str:String){
        igLog(str,"[WARN]  - ")
    }

    /**
     * Add a info error line to be drawed ingame
     */
    fun igLogE(str:String){
        igLog(str,"[ERROR] - s")
    }

    /**
     * Calls the individual game logic to perform the
     * next action
     *
     * This should be non blocking!
     */
    abstract fun requestAction()

    /**
     * Artificially increase the time until the compositor
     * is requested to draw the next frame
     */
    fun addRenderDelay(ms:Long){
        renderDelay+=ms
    }

    /**
     * Artificially increase the time until the next
     * Action is requested
     */
    fun addActionRequestDelay(ms:Long){
        actionRequestDelay+=ms
    }

    /**
     * This function is called before the default
     * notification handling from the LogicCompositor
     * kicks in.
     *
     * Use it in your implementation to catch for example
     * user inputs by subscribing to the ActionHandler
     */
    abstract fun onLCNotify(n: Notification)

    override fun onNotify(n: Notification) {

        //Game specific notifications
        onLCNotify(n)

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

        /**
         * Whatever you want to use it for, this
         * can be used to display log messages
         * inside the running UIComposer (if implemented)
         */
        if(n.type == NotificationType.INGAME_LOG_INFO){
            igLogI(n.str)
            return
        }
        if(n.type == NotificationType.INGAME_LOG_ERROR){
            igLogE(n.str)
            return
        }
        if(n.type == NotificationType.INGAME_LOG_WARN){
            igLogW(n.str)
            return
        }
    }
}