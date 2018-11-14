package abstract.ui

import abstract.GenericCompositor
import flow.ActionHandler
import io.KbdConsole
import jline.internal.Log
import models.Field
import models.HighScore
import models.Notification
import mu.KotlinLogging
import javax.swing.Action

/**
 * Class that is used to draw on the output device
 */
class AsciiCompositor : GenericCompositor{

    private val log = KotlinLogging.logger(this::class.java.name)
    constructor(ah:ActionHandler) : super(ah){
        //The input depends on the frontend so we put it here
        val kbdt:KbdConsole = KbdConsole(ah)
        kbdt.start()
    }

    override fun onSIGINT() {
        //pass
    }

    override fun gameValue(name: String, value: Float) {
    }

    override fun field(field: Field) {
    }

    override fun highScore(highscore: HighScore) {
    }

    override fun clear() {
    }
}