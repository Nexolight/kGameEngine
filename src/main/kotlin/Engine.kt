import abstracted.LogicCompositor
import abstracted.ui.AsciiCompositor
import abstracted.UICompositor
import abstracted.entity.presets.TextEntity
import abstracted.ui.DummyCompositor
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.Pool
import games.snake.SnakeGameLogic
import flow.ActionHandler
import flow.NotifyThread
import models.*
import mu.KotlinLogging
import sun.misc.Signal
import sun.misc.SignalHandler
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Class that handles the game sequence
 */
class Engine : NotifyThread(){
    private val self:Engine = this
    private val log = KotlinLogging.logger(this::class.java.name)


    var kryoPool:Pool<Kryo> = object : Pool<Kryo>(true, false, 8) {
        override fun create(): Kryo {
            val kryo:Kryo = Kryo()
            kryo.isRegistrationRequired = false
            kryo.register(ConcurrentLinkedDeque::class.java)
            kryo.register(LinkedList::class.java)
            kryo.register(ArrayList::class.java)
            kryo.register(HashMap::class.java)
            kryo.register(Buff::class.java)
            kryo.register(HighScore::class.java)
            kryo.register(Position::class.java)
            kryo.register(Rotation::class.java)
            kryo.register(Size::class.java)
            kryo.register(Field::class.java)
            kryo.register(TextEntity::class.java)
            kryo.register(AdvancedQube::class.java)
            return kryo
        }
    }


    private val ah: ActionHandler = ActionHandler()
    private val uic:UICompositor = AsciiCompositor(ah,kryoPool)//DummyCompositor(ah,kryoPool)
    private val lc:LogicCompositor = SnakeGameLogic(ah,kryoPool)

    /**
     * Start the game
     */
    override fun run(){
        log.info { "Engine started!" }
        ah.start()
        //uic(n) : lc(0-1)
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