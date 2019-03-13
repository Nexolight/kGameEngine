package games.snake

import java.nio.file.Path
import java.nio.file.Paths

class SnakeDefaultParams {
    companion object {
        val mapwidth:Int = 50
        val mapheight:Int = 30

        /**
         * Welcome message and timer before start
         */
        val welcomeTimer:Int = 5;
        val welcomeMsg:String = "Starting ksnake in:\n\n "

        /**
         * Game over message and timer before end
         */
        val gameOverTimer:Int = 5
        val gameOverMsg:String = "You bit yourself\n\nGAME OVER\n\nExit in: "

        /**
         * The path where the highscore is persisted
         */
        val highScorePath:Path = Paths.get(System.getProperty("user.home").plus("/.local/share/ksnake/highscore.json"))

        /**
         * delay in ms before the next logic tick
         * lower values increase the game speed
         * @Deprecated
         */
        val tickSpeed:Long = 64

        /**
         * Interval for buff spawns
         */
        val buffSpawnIntervall:Long = 20000

        /**
         * By how much the playerLogic controlled snake
         * is feed at the beginning
         */
        val initialFeed:Int = 10

        /**
         * Base increase in points per food piece
         */
        val pointsPerFood = 50

        /**
         * keyboard ontrols
         */
        val ctrlFWD:Char = 'w'
        val ctrlLEFT:Char = 'a'
        val ctrlBWD:Char = 's'
        val ctrlRIGHT:Char = 'd'

        val asciiHEAD:Char = '@'
        val asciiBODY:Char = '0'
        val asciiTailMvLeft:Char = '/'
        val asciiTailMvRight:Char = '\\'
        val asciiTailMvUp:Char = 'I'
        val asciiTailMvDown:Char = 'I'
    }
}