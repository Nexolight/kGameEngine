package abstracted.ui

import abstracted.UICompositor
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.Pool
import flow.ActionHandler
import models.Field
import models.HighScore
import mu.KLogger
import mu.KotlinLogging
import java.util.concurrent.CopyOnWriteArrayList

class DummyCompositor(ah: ActionHandler, kryoPool: Pool<Kryo>) : UICompositor(ah,kryoPool){
    lateinit var log: KLogger
    override fun onRun() {
        log = KotlinLogging.logger(this::class.java.name)
        log.info { "DummyCompositor started!" }
    }

    override fun onSIGINT() {
        log.info { "DummyCompositor killed!" }
    }

    override fun gameValue(name: String, value: Float) {}

    override fun drawField(field: Field) {}

    override fun drawFPS(fps: Long) {}

    override fun drawLog(logWindow: CopyOnWriteArrayList<String>) {
        for(loginfo:String in logWindow){
            log.info { loginfo }
        }
    }

    override fun highScore(highscore: HighScore) {}

    override fun clear() {}
}