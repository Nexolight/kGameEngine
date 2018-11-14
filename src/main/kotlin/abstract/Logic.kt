package abstract

import flow.ActionHandler
import models.CtrlSequence

enum class ControllerType(){
    AI,PLAYER
}

abstract class Logic(ah:ActionHandler) : Runnable{
    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}