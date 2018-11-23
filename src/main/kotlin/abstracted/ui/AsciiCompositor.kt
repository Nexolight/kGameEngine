package abstracted.ui

import abstracted.Entity
import abstracted.UICompositor
import flow.ActionHandler
import io.KbdConsole
import jline.console.ConsoleReader
import models.BaseUnits
import models.Field
import models.HighScore
import models.SimpleQube
import mu.KotlinLogging
import kotlin.math.roundToInt

/**
 * 2 Dimensional renderer. Can't be used with 3 Dimensional
 * Implementations
 */
class AsciiCompositor(ah:ActionHandler) : UICompositor(ah){
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

        //clear the console, it would be a mess otherwise
        cr.clearScreen()
        cr.flush()

        //simplified field width/height
        val tHeight:Int = (field.height/BaseUnits.ONE).toInt()
        val tWidth:Int = (field.width/BaseUnits.ONE).toInt()

        //iterate trough everything on the field
        for(e:Entity in field.entities){

            //Get the blocks that are visible
            for(block:SimpleQube in e.occupiesSimple()){

                //A simplified offset that is the position of the object within the field.
                val offsetX:Int = (block.pos.x/BaseUnits.ONE).toInt()
                val offsetY:Int = (block.pos.y/BaseUnits.ONE).toInt()

                //Simplified block sizes.
                val bWidth:Int = (block.width/BaseUnits.ONE).toInt()
                val bHeight:Int = (block.height/BaseUnits.ONE).toInt()

                //Fill the space that is occupied by the qube
                for(y in 1..bHeight){
                    for(x in 1..bWidth){
                        val y:Int = offsetY+y
                        val x:Int = offsetX+x
                        System.out.print(String.format("%c[%d;%df",ESC,y+1,x+1))//console cursor position
                        System.out.print(BLOCK)//ASCII character
                    }
                }
            }
        }
    }

    override fun highScore(highscore: HighScore) {
    }

    override fun clear() {
    }
}