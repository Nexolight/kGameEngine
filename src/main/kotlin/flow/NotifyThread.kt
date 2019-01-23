package flow

import models.Notification

abstract class NotifyThread : Thread(){
    abstract fun onNotify(n:Notification)
    abstract override fun run() //force implementation
}