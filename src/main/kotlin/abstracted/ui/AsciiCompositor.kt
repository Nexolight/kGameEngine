package abstracted.ui

import abstracted.Entity
import abstracted.UICompositor
import flow.ActionHandler
import io.KbdConsole
import jline.console.ConsoleReader
import models.Field
import models.HighScore
import models.SimpleQube
import mu.KotlinLogging

/**
 * 2 Dimensional renderer. Can't be used with 3 Dimensional
 * Implementations
 */
class AsciiCompositor(ah:ActionHandler,ccols:Int = 50,crows:Int = 50) : UICompositor(ah){
    private val ccols:Int = ccols
    private val crows:Int = crows
    private val cr:ConsoleReader = ConsoleReader()

    companion object {
        val ESC:Char = 0x1B.toChar()
        val BLOCK:Char = '#'
    }
    private val log = KotlinLogging.logger(this::class.java.name)

    override fun onRun() {
        //Use our frontend specific input handler
        val kbdt:KbdConsole = KbdConsole(ah)
        kbdt.start()
    }

    override fun onSIGINT() {
        //pass
    }

    override fun gameValue(name: String, value: Float) {
    }

    override fun field(field: Field) {
        cr.clearScreen()
        cr.flush()

        for(e:Entity in field.entities){
            for(block:SimpleQube in e.occupiesSimple()){
                //-> wrong unit, to many rows & cols
                System.out.print(String.format("%c[%d;%df",ESC,block.pos.x.toInt(),block.pos.y.toInt()))
                System.out.print(BLOCK)
            }
        }
    }

    override fun highScore(highscore: HighScore) {
    }

    override fun clear() {
    }
}