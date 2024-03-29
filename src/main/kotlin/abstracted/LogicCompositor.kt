package abstracted

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.Pool
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import flow.ActionHandler
import flow.NotifyThread
import models.*
import mu.KLogger
import mu.KotlinLogging
import physics.Collision
import physics.`if`.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read

abstract class LogicCompositor(ah:ActionHandler, kryoPool: Pool<Kryo>) : NotifyThread(){
    companion object {
        val maxLogLen:Int = 5//TODO: parameter
    }

    lateinit var log: KLogger
    private var kill = false
    protected val ah:ActionHandler = ah
    private val uics:ConcurrentLinkedDeque<UICompositor> = ConcurrentLinkedDeque<UICompositor>()
    protected val kryoPool:Pool<Kryo> = kryoPool
    private val executorService:ListeningExecutorService = MoreExecutors.listeningDecorator(
            Executors.newCachedThreadPool()
    )
    var asycnCollision:Boolean = true

    /**
     * The field must be deepcopied before sending
     * it to the UIComposer.
     *
     * This should be done after an action request
     * that alters this field with every run.
     *
     */
    abstract var field:Field
    private val fieldLock = ReentrantReadWriteLock()

    /**
     * Either we deepcopy the list we send to the UIC or
     * we use a CopyOnWriteArrayList which is expensive
     * but acceptable for this purpose.
     */
    private val iglog:CopyOnWriteArrayList<String> = CopyOnWriteArrayList<String>()

    private var actionRequestDelay:Long = 0
    var actionRequestTicks:Long = 0
        private set

    override fun run(){
        log = KotlinLogging.logger(this::class.java.name)
        log.info { "LogicCompositor started!" }
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.subscribeNotification(Notification(this,NotificationType.UI_COMPOSITOR_AVAILABLE))
        ah.subscribeNotification(Notification(this,NotificationType.INGAME_LOG_INFO))
        ah.subscribeNotification(Notification(this,NotificationType.INGAME_LOG_WARN))
        ah.subscribeNotification(Notification(this,NotificationType.INGAME_LOG_ERROR))
        ah.notify(Notification(this,NotificationType.NEW_LOGIC_COMPOSITOR_AVAILABLE,this))
        var updated:Boolean = false
        while(!kill){

            requestAction()
            ++actionRequestTicks

            //serialize for UIComposers
            val kryo:Kryo = kryoPool.obtain()
            val serialField:ByteArrayOutputStream = ByteArrayOutputStream()
            val kryoOut:Output = Output(serialField)
            fieldLock.read {
                kryo.writeObject(kryoOut,field)
            }
            kryoOut.flush()
            kryoOut.close()

            //Start the collision detection
            asyncCollisionDetection(serialField.toByteArray())

            //Multiplex too all UIComposers
            for(uic in uics){
                if(uic.isAlive){
                    uic.onLCReady(serialField.toByteArray())
                    uic.onLCReady(iglog)
                }
            }
            kryoPool.free(kryo)

            if(actionRequestDelay > 0){
                Thread.sleep(actionRequestDelay)
                actionRequestDelay = 0
            }else{
                Thread.sleep(1)//limit at 1000
            }
        }
        log.info { "LogicCompositor stopped gracefully!" }
    }

    /**
     * Colliders are usually one of the most heavy parts to process
     * thus we're gonna try to do this async and see how it turns out.
     *
     * Worst case would be a delayed collision that could be corrected
     * after the collision happened.
     */
    private fun asyncCollisionDetection(serializedField: ByteArray){
        if(asycnCollision){
            executorService.submit(Callable{
                val kryo:Kryo = kryoPool.obtain()
                val kryoIn: Input = Input(ByteArrayInputStream(serializedField))
                val fieldSnapshot:Field = kryo.readObject(kryoIn,Field::class.java)
                kryoIn.close()
                kryoPool.free(kryo)
                syncCollisionDetection(fieldSnapshot)
            })
        }else{
            val kryo:Kryo = kryoPool.obtain()
            val kryoIn: Input = Input(ByteArrayInputStream(serializedField))
            val fieldSnapshot:Field = kryo.readObject(kryoIn,Field::class.java)
            kryoIn.close()
            kryoPool.free(kryo)
            syncCollisionDetection(fieldSnapshot)
        }
    }

    /**
     * Synchron collision detection
     * TODO: Use some proper math or a physic library.
     */
    private fun syncCollisionDetection(fieldSnapshot:Field){
        for(entity:Entity in fieldSnapshot.entities){

            /**
             * We only want to check for collisions on rigid bodys
             */
            if(entity is RigidBody){
                /**
                 * Need to check intersection with all colliders
                 * TODO: add filter bounds
                 */
                for(collider in fieldSnapshot.entities){
                    if(collider is OrthogonCollider){
                        //TODO: OrthogonCollider implementation
                        for(colliderBlock in collider.getColliderBlocks()){
                            if(entity.intersectsRigidBody(colliderBlock)){
                                //igLogI("COLLISION")
                                ah.notify(Notification(
                                        this,
                                        NotificationType.COLLISION,
                                        Collision(collider, entity)
                                ))
                            }
                        }
                    }else if(collider is PositionalCollider){
                        for(colliderPos in collider.getColliderPositions()){
                            if(entity.intersectsRigidBody(colliderPos)){
                                //igLogI("COLLISION")
                                ah.notify(Notification(
                                        this,
                                        NotificationType.COLLISION,
                                        Collision(collider, entity)
                                ))
                            }
                        }

                    }
                    //TODO: add other types of colliders
                }
            }
        }
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
            uics.add(n.uic)
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