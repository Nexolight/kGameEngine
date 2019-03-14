package models

import abstracted.LogicCompositor
import abstracted.UICompositor
import flow.NotifyThread
import physics.Collision
import java.util.*


enum class NotificationType{
    MOVEMENT,
    INTERACTION,
    COLLISION,
    USERINPUT,
    SIGNAL,
    GAMESIGNAL,
    FPSCAP,
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
        val uic:UICompositor?,
        val collision: Collision?,
        val pair:NotifyPair<Any>?){

    /**
     * Just notify without any value
     */
    constructor(thread:NotifyThread, type:NotificationType) :
            this(thread,type,0.toChar(),"",0,null,null,null,null) {}

    /**
     * Notify with an integer
     */
    constructor(thread:NotifyThread, type:NotificationType, n:Int) :
            this(thread,type,0.toChar(),"",n,null,null,null,null){}

    /**
     * Notify with a Char
     */
    constructor(thread:NotifyThread, type:NotificationType, chr:Char) :
            this(thread,type,chr,"",0,null,null,null,null){}

    /**
     * Notify with a String
     */
    constructor(thread:NotifyThread, type:NotificationType, str:String) :
            this(thread,type,0.toChar(),str,0,null,null,null,null){}


    /**
     * Notify with a LogicCompositor
     */
    constructor(thread:NotifyThread, type:NotificationType, lc:LogicCompositor) :
            this(thread, type, 0.toChar(),"",0,lc,null,null,null){}

    /**
     * Notify with a UICOmpositor
     */
    constructor(thread:NotifyThread, type:NotificationType, ulc:UICompositor) :
            this(thread, type, 0.toChar(),"",0,null,ulc,null,null){}

    /**
     * Notify with an Entity
     */
    constructor(thread:NotifyThread, type:NotificationType, collision: Collision) :
            this(thread, type, 0.toChar(),"",0,null,null,collision,null){}

    /**
     * Notify with a pair
     */
    constructor(thread:NotifyThread, type:NotificationType, pair: NotifyPair<Any>) :
            this(thread, type, 0.toChar(),"",0,null,null,null,pair){}
}

class NotifyPair<out B>(val first:Int = -1, val second:B? = null){}
