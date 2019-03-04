package abstracted.ui

import abstracted.Entity
import abstracted.UICompositor
import abstracted.ui.`if`.ASCIISupport
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.Pool
import flow.ActionHandler
import io.KbdConsole
import jline.console.ConsoleReader
import models.*
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToInt

/**
 * 2 Dimensional renderer.
 * Only compatible with Entities that implement
 * ASCIISupport
 *
 * TODO: implement rotation
 */
class AsciiCompositor(ah:ActionHandler,kryoPool: Pool<Kryo>) : UICompositor(ah,kryoPool){
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
        //clear the console, it would be a mess otherwise
        cr.clearScreen()
        cr.flush()
    }

    override fun gameValue(name: String, value: Float) {
    }

    override fun drawField(field: Field) {
        //update field borders
        maxFieldWidht=(field.width/BaseUnits.ONE).toInt()
        maxFieldHeight=(field.height/BaseUnits.ONE).toInt()

        //simplified drawField width/height
        //val tHeight:Int = (field.height/BaseUnits.ONE).toInt()
        //val tWidth:Int = (field.width/BaseUnits.ONE).toInt()

        //iterate trough everything on the drawField
        var draw:String=""
        for(e:Entity in field.entities){

            if(e !is ASCIISupport){
                //TODO: Warn about unsupported entities
                continue
            }

            //Get the blocks that are visible
            for(block:AdvancedQube in e.occupies()){

                //A simplified offset that is the position of the object within the drawField.
                val offsetX:Int = (block.pos.x/BaseUnits.ONE).toInt()
                val offsetY:Int = (block.pos.y/BaseUnits.ONE).toInt()

                //Simplified block sizes.
                val bWidth:Int = (block.size.width/BaseUnits.ONE).toInt()
                val bHeight:Int = (block.size.height/BaseUnits.ONE).toInt()

                //Fill the space that is occupied by the qube
                for(y in 1..bHeight){
                    for(x in 1..bWidth){
                        val y:Int = offsetY+y
                        val x:Int = offsetX+x
                        draw+=String.format("%c[%d;%df",ESC,y+WINDOW_GAME_Y_OFFSET+1,x+1)//console cursor position
                        draw+=e.getOccupyRepresentation(block.pos,block.rota)//ASCII character
                    }
                }
            }
        }
        //clear the console, it would be a mess otherwise
        cr.clearScreen()
        cr.flush()
        System.out.print(draw)
    }

    override fun drawLog(logWindow: CopyOnWriteArrayList<String>) {
        var y:Int = WINDOW_LOG_Y_OFFSET+maxFieldHeight
        var draw:String=""
        draw+=String.format("%c[%d;%df",ESC, y,WINDOW_LOG_X_OFFSET+1)//console cursor position
        draw+="[LOGS]"
        draw+=String.format("%c[%d;%df",ESC, y+1,WINDOW_LOG_X_OFFSET+1)//console cursor position
        draw+="-".repeat(maxFieldWidht)

        for(ll in logWindow){
            draw+=String.format("%c[%d;%df",ESC, y+2,WINDOW_LOG_X_OFFSET+1)//console cursor position
            draw+=ll
            ++y
        }
        System.out.print(draw)
    }

    override fun drawFPS(fps: Long) {
        var draw:String = ""
        draw+=String.format("%c[%d;%df",ESC, WINDOW_INFO_Y_OFFSET+1,WINDOW_INFO_FPS_X_OFFSET+1)//console cursor position
        draw+="fps: $fps"
        System.out.print(draw)
    }

    override fun highScore(highscore: HighScore) {
    }

    override fun clear() {
    }
}