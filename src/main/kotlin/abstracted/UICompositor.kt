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
    protected val rqField:ConcurrentLinkedQueue<Field> = ConcurrentLinkedQueue<Field>()
    protected val rqLog:ConcurrentLinkedQueue<Deque<String>> = ConcurrentLinkedQueue<Deque<String>>()
    protected lateinit var lc:LogicCompositor
    private var fpsTimeFrame:Long = 0
    private var renderedFrames:Long = 0
    private var fps:Long = 0

    override fun run() {
        log.info { "AsciiCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.NEW_LOGIC_COMPOSITOR_AVAILABLE))
        ah.notify(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE,this))
        fpsTimeFrame = System.currentTimeMillis()
        while(!kill){
            while(rqField.isNotEmpty()){
                if(rqField.size >= 4){
                    frameSkip(rqField.size - (rqField.size-1))
                }
                drawField(rqField.poll())
                drawLog(rqLog.poll())

                //Calculate & draw fps if enabled
                //TODO: add argument
                ++renderedFrames
                if(System.currentTimeMillis()-fpsTimeFrame>=1000){
                    fps = renderedFrames
                    renderedFrames=0
                    fpsTimeFrame=System.currentTimeMillis()
                }
                drawFPS(fps)

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
            rqField.poll()
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
     * when calculations are done and ready to be
     * visualized
     */
    fun onLCReady(f:Field){
        rqField.add(f)
    }

    /**
     * Called by the registerd LogicCompositor
     * when calculations are done and ready to be
     * visualized
     */
    fun onLCReady(logWindow:Deque<String>){
        rqLog.add(logWindow)
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
    protected abstract fun drawField(field: Field)

    /**
     * Draws the current fps rate
     */
    protected abstract fun drawFPS(fps:Long)

    /**
     * Draws all the lines in the passed log list
     */
    protected abstract fun drawLog(logWindow:Deque<String>)

    /**
     * Draws the highscore
     */
    protected abstract fun highScore(highscore: HighScore)

    /**
     * Clears the drawing area
     */
    protected abstract fun clear()
}