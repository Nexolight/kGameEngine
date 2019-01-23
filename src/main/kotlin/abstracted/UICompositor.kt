package abstracted

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.util.Pool
import com.sun.org.apache.xpath.internal.operations.Bool
import flow.ActionHandler
import flow.NotifyThread
import models.Field
import models.HighScore
import models.Notification
import models.NotificationType
import mu.KotlinLogging
import java.io.ByteArrayInputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

enum class CompositorType{
    ascii,web,qt
}

/**
 * Class that is used to draw on the output device
 */
abstract class UICompositor(ah:ActionHandler, kryoPool: Pool<Kryo>) : NotifyThread(){
    private val log = KotlinLogging.logger(this::class.java.name)
    private var kill = false
    protected val ah:ActionHandler = ah
    protected val kryoPool:Pool<Kryo> = kryoPool
    protected val rqField:Queue<Field> = ConcurrentLinkedQueue<Field>()
    protected val rqLog:Queue<CopyOnWriteArrayList<String>> = ConcurrentLinkedQueue<CopyOnWriteArrayList<String>>()

    private lateinit var self:UICompositor

    private var renderedFrames:Long = 0
    private var lastFPSUpdate:Long = 0
    private var lastDraw:Long = 0
    private var fps:Long = 0

    private var reNotifyUIC:Boolean = false

    override fun run() {
        log.info { "AsciiCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.NEW_LOGIC_COMPOSITOR_AVAILABLE))
        ah.notify(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE,this))
        lastDraw = System.currentTimeMillis()
        lastFPSUpdate = System.currentTimeMillis()

        //Call Compositor specific run method
        onRun()


        while(!kill || rqField.isNotEmpty()) {

            if(reNotifyUIC){
                ah.notify(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE,this))
                reNotifyUIC=false
            }

            //Framecap
            val vsyncDelta:Long = 16 - (System.currentTimeMillis() - lastDraw)
            if(vsyncDelta > 0){
                Thread.sleep(vsyncDelta)
            }

            //Frameskip
            if (rqField.size >= 4) {
                frameSkip(rqField.size - (rqField.size - 1))
            }

            if(rqField.size > 0){
                drawField(rqField.poll())
            }


            if (rqLog.size > 0) {
                drawLog(rqLog.poll())
            }

            drawFPS(fps)

            lastDraw = System.currentTimeMillis()

            //Calculate & draw fps if enabled
            //TODO: add argument
            ++renderedFrames
            if(System.currentTimeMillis()-lastFPSUpdate>=1000){
                fps = renderedFrames
                renderedFrames=0
                lastFPSUpdate=System.currentTimeMillis()
            }

        }
        //TODO: Implement better VSYNC
        onSIGINT()
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
            log.info { "New LogicCompositor noticed, sending notification" }
            //Notify the logic compositor that a consumer is available
            reNotifyUIC=true
            return
        }
        if(n.type == NotificationType.SIGNAL && n.n == 2){
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
    fun onLCReady(serializedField: ByteArray){
        val kryo:Kryo = kryoPool.obtain()
        val kryoIn:Input = Input(ByteArrayInputStream(serializedField))
        rqField.add(kryo.readObject(kryoIn,Field::class.java))
        kryoIn.close()
        kryoPool.free(kryo)
    }

    /**
     * Called by the registerd LogicCompositor
     * when calculations are done and ready to be
     * visualized
     */
    fun onLCReady(logWindow:CopyOnWriteArrayList<String>){
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
     * Draws the current entities.Field to the output device
     */
    protected abstract fun drawField(field: Field)

    /**
     * Draws the current fps rate
     */
    protected abstract fun drawFPS(fps:Long)

    /**
     * Draws all the lines in the passed log list
     */
    protected abstract fun drawLog(logWindow:CopyOnWriteArrayList<String>)

    /**
     * Draws the highscore
     */
    protected abstract fun highScore(highscore: HighScore)

    /**
     * Clears the drawing area
     */
    protected abstract fun clear()
}