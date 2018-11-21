package models

import abstracted.LogicCompositor
import abstracted.UICompositor
import flow.NotifyThread


enum class NotificationType{
    MOVEMENT,
    INTERACTION,
    USERINPUT,
    SIGNAL,
    NEW_LOGIC_COMPOSITOR_AVAILABLE,
    UI_COMPOSITOR_AVAILABLE
}

class Notification(
        val thread:NotifyThread,
        val type:NotificationType,
        val chr:Char,
        val n:Int,
        val lc:LogicCompositor?,
        val uic:UICompositor?){

    constructor(thread:NotifyThread, type:NotificationType) : this(thread,type,0.toChar(),0,null,null) {}
    constructor(thread:NotifyThread, type:NotificationType, n:Int) : this(thread,type,0.toChar(),n,null,null){}
    constructor(thread:NotifyThread, type:NotificationType, chr:Char) : this(thread,type,chr,0,null,null){}
    constructor(thread:NotifyThread, type:NotificationType, lc:LogicCompositor) : this(thread,type,0.toChar(),0,lc,null){}
    constructor(thread:NotifyThread, type:NotificationType, ulc:UICompositor) : this(thread,type,0.toChar(),0,null,ulc){}

}
