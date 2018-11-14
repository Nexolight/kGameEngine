import models.Field
import abstract.ui.AsciiCompositor
import abstract.GenericCompositor
import flow.ActionHandler
import flow.NotifyThread
import models.Notification
import models.NotificationType
import mu.KotlinLogging
import sun.misc.Signal
import sun.misc.SignalHandler


enum class ThreadState{
    IO_FAIL, IO_CLOSED,
    DRAW_FAIL, DRAW_CLOSED
}

/**
 * Class that handles the game sequence
 */
class Engine : NotifyThread(){
    private val self:Engine = this
    private val log = KotlinLogging.logger(this::class.java.name)
    private val field: Field = Field()
    private val ah: ActionHandler = ActionHandler()
    private val compositor:GenericCompositor = AsciiCompositor(ah)
    //private val playerLogic:Logic = PlayerLogic(ah)
    //private val spawnLogic:Logic = SpawnLogic(ah)

    /**
     * Start the game
     */
    fun exec(){
        log.info { "Engine started!" }
        field.reset()
        ah.start()
        compositor.start()

        Signal.handle(Signal("INT"), object : SignalHandler {
            override fun handle(sig: Signal) {
                ah.notify(Notification(self,NotificationType.SIGNAL,2))
                log.info { "Awaiting termination..." }
                while(ah.isAlive || compositor.isAlive){
                    Thread.sleep(16)
                }
                log.info { "All threads terminated gracefully - Quit" }
                System.exit(0)
            }
        })

        //service.submit(playerLogic)
        //service.submit(spawnLogic)

    }

    override fun onNotify(n: Notification) {
        //pass
    }
}