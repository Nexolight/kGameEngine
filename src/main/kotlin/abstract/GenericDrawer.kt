package abstract

import models.Field
import models.HighScore

/**
 * Class that is used to draw on the output device
 */
abstract class GenericDrawer : Runnable{
    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Draws an additional informative game value
     */
    abstract fun gameValue(name:String,value:Float);

    /**
     * Draws the current models.Field to the output device
     */
    abstract fun field(field: Field);

    /**
     * Draws the highscore
     */
    abstract fun highScore(highscore: HighScore);

    /**
     * Clears the drawing area
     */
    abstract fun clear();
}