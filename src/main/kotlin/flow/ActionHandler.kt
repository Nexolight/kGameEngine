package flow

import models.Notification
import models.NotificationType
import mu.KLogger
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

/**
 * This class deals with actions that
 * entities want to perform
 */
class ActionHandler : NotifyThread(){
    lateinit var log: KLogger
    private var kill = false
    private val pendingLock = ReentrantReadWriteLock()
    private val notifyLock = ReentrantReadWriteLock()

    //TODO: unoptimal datatypes should be a queue or smth.
    private val pending: Deque<Notification> = ConcurrentLinkedDeque<Notification>()

    private val notifier: Deque<Notification> = ConcurrentLinkedDeque<Notification>()

    override fun run() {
        log = KotlinLogging.logger(this::class.java.name)
        log.info { "ActionHandler started!" }

        //we will be informed about termination
        subscribeNotification(Notification(this,NotificationType.SIGNAL))

        while(!kill){
            notifyLock.write {
                for(np in pending){
                    for(ntf in notifier){
                        if(np.type == ntf.type){
                            ntf.thread.onNotify(np)
                        }
                    }
                }
                pending.clear()
            }
            Thread.sleep(1)
        }
        log.info { "ActionHandler stopped gracefully!" }
    }

    override fun onNotify(n: Notification) {
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            log.info { "Killing ActionHandler!" }
            kill = true
        }
    }

    /**
     * Notifies all subscribed other threads
     */
    fun notify(notification:Notification){
        pendingLock.write {
            pending.add(notification)
        }
    }

    /**
     * Allows a thread to get notified about a certain type of
     * action performed
     */
    fun subscribeNotification(notification:Notification){
        notifyLock.write {
            notifier.add(notification)
        }
    }

    /**
     * Removes notifications for a thread for a certain type of
     * actions
     */
    fun unsubscribeNotification(notification:Notification){
        notifyLock.write {
            notifier.remove(notification)
        }
    }
}