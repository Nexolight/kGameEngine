package models

import flow.NotifyThread


enum class NotificationType{
    MOVEMENT,INTERACTION,USERINPUT,SIGNAL,FIELD
}

class Notification(
        val thread:NotifyThread,
        val type:NotificationType,
        val chr:Char,
        val n:Int,
        val field:Field?){

    constructor(thread:NotifyThread, type:NotificationType) : this(thread,type,0.toChar(),0,null) {}
    constructor(thread:NotifyThread, type:NotificationType, n:Int) : this(thread,type,0.toChar(),n,null){}
    constructor(thread:NotifyThread, type:NotificationType, chr:Char) : this(thread,type,chr,0,null){}
    constructor(thread:NotifyThread, type:NotificationType, field:Field) : this(thread,type,0.toChar(),0,field){}
}
