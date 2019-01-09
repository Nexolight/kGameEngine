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
    UI_COMPOSITOR_AVAILABLE,
    INGAME_LOG_INFO,
    INGAME_LOG_ERROR,
    INGAME_LOG_WARN
}

class Notification(
        val thread:NotifyThread,
        val type:NotificationType,
        val chr:Char,
        val str:String,
        val n:Int,
        val lc:LogicCompositor?,
        val uic:UICompositor?){

    /**
     * Just notify without any value
     */
    constructor(thread:NotifyThread, type:NotificationType) : this(thread,type,0.toChar(),"",0,null,null) {}

    /**
     * Notify with an integer
     */
    constructor(thread:NotifyThread, type:NotificationType, n:Int) : this(thread,type,0.toChar(),"",n,null,null){}

    /**
     * Notify with a Char
     */
    constructor(thread:NotifyThread, type:NotificationType, chr:Char) : this(thread,type,chr,"",0,null,null){}

    /**
     * Notify with a String
     */
    constructor(thread:NotifyThread, type:NotificationType, str:String) : this(thread,type,0.toChar(),str,0,null,null){}


    /**
     * Notify with a LogicCompositor
     */
    constructor(thread:NotifyThread, type:NotificationType, lc:LogicCompositor) : this(thread,type,0.toChar(),"",0,lc,null){}

    /**
     * Notify with a UICOmpositor
     */
    constructor(thread:NotifyThread, type:NotificationType, ulc:UICompositor) : this(thread,type,0.toChar(),"",0,null,ulc){}

}
