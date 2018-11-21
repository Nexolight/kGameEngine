package abstracted

import flow.ActionHandler
import flow.NotifyThread
import models.Field
import models.HighScore
import models.Notification
import models.NotificationType
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

enum class CompositorType{
    ascii,web,qt
}

/**
 * Class that is used to draw on the output device
 */
abstract class UICompositor(ah:ActionHandler) : NotifyThread(){
    private val log = KotlinLogging.logger(this::class.java.name)
    private var kill = false
    protected val ah:ActionHandler = ah
    protected val rq:ConcurrentLinkedQueue<Field> = ConcurrentLinkedQueue<Field>()
    protected lateinit var lc:LogicCompositor


    override fun run() {
        log.info { "AsciiCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.NEW_LOGIC_COMPOSITOR_AVAILABLE))
        ah.notify(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE,this))
        while(!kill){
            while(rq.isNotEmpty()){
                if(rq.size >= 4){
                    frameSkip(rq.size - (rq.size-1))
                }
                field(rq.poll())
            }
            //TODO: Better sync
        }
        log.info { "UICompositor stopped gracefully!" }
    }

    /**
     * Throws away the given delta of frames to catch up
     */
    private fun frameSkip(delta:Int){
        //log.warn { "Throwing away "+delta.toString()+" frames" }
        for(i in 0 until delta){
            rq.poll()
        }
    }

    /**
     * Will be called once after the UICompositor has initialized
     * to enable the Specific implementation to do it's stuff
     */
    abstract fun onRun()

    override fun onNotify(n: Notification) {
        if(n.type == NotificationType.NEW_LOGIC_COMPOSITOR_AVAILABLE && n.lc != null){
            lc = n.lc
            log.info { "New LogicCompositor noticed" }
            //Notify the logic compositor that a consumer is available
            ah.notify(Notification(this, NotificationType.UI_COMPOSITOR_AVAILABLE,this))
            return
        }
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            onSIGINT()
            log.info { "Killing UICompositor" }
            kill = true
            return
        }
    }

    /**
     * Called by the registered LogicCompositor
     * whn calculations are done and ready to be
     * visualized
     */
    fun onLCReady(f:Field){
        rq.add(f)
    }

    /**
     * Called when the compositor was asked to shutdown
     */
    protected abstract fun onSIGINT()

    /**
     * Draws an additional informative game value
     */
    protected abstract fun gameValue(name:String,value:Float)

    /**
     * Draws the current models.Field to the output device
     */
    protected abstract fun field(field: Field)

    /**
     * Draws the highscore
     */
    protected abstract fun highScore(highscore: HighScore)

    /**
     * Clears the drawing area
     */
    protected abstract fun clear()
}