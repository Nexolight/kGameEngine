package flow

import models.Notification
import java.util.concurrent.ConcurrentLinkedQueue

abstract class NotifyThread : Thread(){
    abstract fun onNotify(n:Notification)
    abstract override fun run() //force implementation
}