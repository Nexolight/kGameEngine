import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


enum class ThreadState{
    IO_FAIL, IO_CLOSED,
    DRAW_FAIL, DRAW_CLOSED
}
/**
 * Class that handles the game sequence
 */
class Engine {
    private val executor:ExecutorService = Executors.newFixedThreadPool(3)
    private val service:ListeningExecutorService = MoreExecutors.listeningDecorator(executor)
    private val field:Field = Field()
    private val drawer:Drawer = Drawer()
    private val ctrl:CtrlSequence = CtrlSequence()
    private val logic:SnakeLogic = SnakeLogic()

    /**
     * Start the game
     */
    fun start(){
        field.resetAll()
        service.submit(ctrl)
        service.submit(drawer)
        service.submit(logic)
    }
}