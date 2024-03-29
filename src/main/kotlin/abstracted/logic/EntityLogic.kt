package abstracted.logic

import flow.NotifyThread

/**
 * An EntityLogic child should contain an entity
 * that it controls in it's own thread
 */
abstract class EntityLogic() : NotifyThread() {

    /**
     * The caller trhead must not handle the logic
     * of an individual entity thread
     *
     * Thus a property is used to listen on.
     */
    private var aR:Boolean = false

    /**
     * The LogicCompositor calls the
     * actionRequest function inside the
     * games individual logic.
     *
     * The game's individual logic then is supposed
     * to call this function on EntityLogic components
     * at a 1:1 ratio or less
     */
    fun actionRequest(){
        aR = true
    }

    /**
     * Indicates that the ActionRequest was handled
     */
    protected fun actionRequestDone(){
        aR = false
    }

    /**
     * Indicates that an ActionRequest needs to be handled
     */
    fun actionRequestPending() : Boolean{
        return aR
    }
}