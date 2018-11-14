package flow

import models.Notification

abstract class NotifyThread : Thread(){
    abstract public fun onNotify(n:Notification)
}