import abstracted.LogicCompositor
import abstracted.ui.AsciiCompositor
import abstracted.UICompositor
import games.snake.SnakeGameLogic
import flow.ActionHandler
import flow.NotifyThread
import models.Notification
import models.NotificationType
import mu.KotlinLogging
import sun.misc.Signal
import sun.misc.SignalHandler

/**
 * Class that handles the game sequence
 */
class Engine : NotifyThread(){
    private val self:Engine = this
    private val log = KotlinLogging.logger(this::class.java.name)
    private val ah: ActionHandler = ActionHandler()
    private val uic:UICompositor = AsciiCompositor(ah)
    private val lc:LogicCompositor = SnakeGameLogic(ah)
    //private val playerLogic:Logic = PlayerLogic(ah)
    //private val spawnLogic:Logic = SpawnLogic(ah)

    /**
     * Start the game
     */
    fun exec(){
        log.info { "Engine started!" }
        ah.start()
        uic.start()
        lc.start()

        Signal.handle(Signal("INT"), object : SignalHandler {
            override fun handle(sig: Signal) {
                ah.notify(Notification(self,NotificationType.SIGNAL,2))
                log.info { "Awaiting termination..." }
                while(ah.isAlive || uic.isAlive || lc.isAlive){
                    Thread.sleep(16)
                }
                log.info { "All threads terminated gracefully - Quit" }
                System.exit(0)
            }
        })

    }

    override fun onNotify(n: Notification) {
        //pass
    }
}