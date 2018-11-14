package abstract

import flow.ActionHandler
import flow.NotifyThread
import models.Field
import models.HighScore
import models.Notification
import models.NotificationType
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.swing.Action
import kotlin.concurrent.write

enum class CompositorType{
    ascii,web,qt
}

/**
 * Class that is used to draw on the output device
 */
abstract class GenericCompositor(ah:ActionHandler) : NotifyThread(){
    private val queueLock = ReentrantReadWriteLock()
    private val log = KotlinLogging.logger(this::class.java.name)
    private var kill = false
    protected val ah:ActionHandler = ah
    protected val rq:Deque<Field> = LinkedList<Field>()

    override fun run() {
        log.info { "AsciiCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.FIELD))
        while(!kill){
            if(rq.isNotEmpty()){
                queueLock.write {
                    for(frame:Field in rq){
                        field(frame)
                    }
                }
                rq.clear()
            }
            Thread.sleep(16)
            //TODO: Better sync
        }
        log.info { "GenericCompositor stopped gracefully!" }
    }

    override fun onNotify(n: Notification) {
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            onSIGINT()
            log.info { "Killing GenericCompositor" }
            kill = true
            return
        }
        if(n.type == NotificationType.FIELD && n.field != null){
            queueLock.write {
                rq.add(n.field)
            }
            return
        }
    }

    /**
     * Called when the compositor was asked to shutdown
     */
    abstract fun onSIGINT()

    /**
     * Draws an additional informative game value
     */
    abstract fun gameValue(name:String,value:Float)

    /**
     * Draws the current models.Field to the output device
     */
    abstract fun field(field: Field)

    /**
     * Draws the highscore
     */
    abstract fun highScore(highscore: HighScore)

    /**
     * Clears the drawing area
     */
    abstract fun clear()
}