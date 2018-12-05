package abstracted.ui

import abstracted.Entity
import abstracted.UICompositor
import abstracted.ui.`if`.ASCIISupport
import flow.ActionHandler
import io.KbdConsole
import jline.console.ConsoleReader
import models.BaseUnits
import models.Field
import models.HighScore
import models.SimpleQube
import mu.KotlinLogging
import java.util.*
import kotlin.math.roundToInt

/**
 * 2 Dimensional renderer. Can't be used with 3 Dimensional
 * Implementations
 */
class AsciiCompositor(ah:ActionHandler) : UICompositor(ah){
    private val cr:ConsoleReader = ConsoleReader()

    //Field is dynamic, update for other offsets
    private var maxFieldHeight:Int = 0
    private var maxFieldWidht:Int = 0

    companion object {
        val ESC:Char = 0x1B.toChar()
        val BLOCK:Char = '#'
        val WINDOW_INFO_Y_OFFSET:Int = 0
        val WINDOW_INFO_FPS_X_OFFSET:Int = 1
        val WINDOW_GAME_Y_OFFSET:Int = WINDOW_INFO_Y_OFFSET+2
        val WINDOW_LOG_Y_OFFSET:Int = WINDOW_GAME_Y_OFFSET+3//+dynamic size of field
        val WINDOW_LOG_X_OFFSET:Int = 1
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

    override fun drawField(field: Field) {
        //update field borders
        maxFieldWidht=(field.width/BaseUnits.ONE).toInt()
        maxFieldHeight=(field.height/BaseUnits.ONE).toInt()

        //clear the console, it would be a mess otherwise
        cr.clearScreen()
        cr.flush()

        //simplified drawField width/height
        //val tHeight:Int = (field.height/BaseUnits.ONE).toInt()
        //val tWidth:Int = (field.width/BaseUnits.ONE).toInt()

        //iterate trough everything on the drawField
        for(e:Entity in field.entities){

            if(e !is ASCIISupport){
                //TODO: Warn about unsupported entities
                continue
            }

            //Get the blocks that are visible
            for(block:SimpleQube in e.occupiesSimple()){

                //A simplified offset that is the position of the object within the drawField.
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
                        System.out.print(String.format("%c[%d;%df",ESC,y+WINDOW_GAME_Y_OFFSET+1,x+1))//console cursor position
                        System.out.print(e.getOccupyRepresentation(block.pos))//ASCII character
                    }
                }
            }
        }
    }

    override fun drawLog(logWindow: Deque<String>) {
        var y:Int = WINDOW_LOG_Y_OFFSET+maxFieldHeight
        System.out.print(String.format("%c[%d;%df",ESC, y,WINDOW_LOG_X_OFFSET+1))//console cursor position
        System.out.print("[LOGS]")
        System.out.print(String.format("%c[%d;%df",ESC, y+1,WINDOW_LOG_X_OFFSET+1))//console cursor position
        System.out.print("-".repeat(maxFieldWidht))

        for(ll in logWindow){
            System.out.print(String.format("%c[%d;%df",ESC, y+2,WINDOW_LOG_X_OFFSET+1))//console cursor position
            System.out.print(ll)
            ++y
        }
    }

    override fun drawFPS(fps: Long) {
        System.out.print(String.format("%c[%d;%df",ESC, WINDOW_INFO_Y_OFFSET+1,WINDOW_INFO_FPS_X_OFFSET+1))//console cursor position
        System.out.print("fps: $fps")
    }

    override fun highScore(highscore: HighScore) {
    }

    override fun clear() {
    }
}