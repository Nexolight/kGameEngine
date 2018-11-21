package abstracted.ui

import abstracted.Entity
import abstracted.UICompositor
import flow.ActionHandler
import io.KbdConsole
import models.BaseUnits
import models.Field
import models.HighScore
import models.SimpleRect
import mu.KotlinLogging

/**
 * Class that is used to draw on the output device
 */
class AsciiCompositor(ah:ActionHandler,ccols:Int = 50,crows:Int = 50) : UICompositor(ah){
    private val ccols:Int = ccols
    private val crows:Int = crows

    companion object {
        val ESC:Char = 0x1B.toChar()
        val WALL:Char = '#'
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
        var posX = 0
        var posY = 0
        var unitX = Math.floorDiv(field.width.toInt(),ccols)
        var unitY = Math.floorDiv(field.height.toInt(),crows)


        for(e:Entity in field.entities){
            for(block:SimpleRect in e.occupiesSimple()){
                //...
                System.out.print(String.format(WALL.plus("%c[%d;%df"),ESC,posX,posY))
            }
        }
    }

    override fun highScore(highscore: HighScore) {
    }

    override fun clear() {
    }
}