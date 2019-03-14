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
        val welcomeTimer:Int = 3;
        val welcomeMsg:String = "Starting ksnake in:\n\n "

        /**
         * Game over message and timer before end
         */
        val gameOverTimer:Int = 3
        val gameOverMsg:String = "You bit yourself\n\nGAME OVER\n\nExit in: "

        /**
         * Limit the entries in the highscore
         */
        val highscoreLimit:Int = 10

        /**
         * The string displayed for the reached score asking to type in the name
         */
        val highscoreEntryMsg:String = "> [Type your name]..."

        /**
         * The default name that is used in the highscore when no name was given
         */
        val highscoreDefaultName:String = "Anonymous"

        /**
         * String that is displayed below the highscore
         */
        val highscoreHint:String = "\n"+
                "if you get \"${highscoreEntryMsg}\":\n\n" +
                "> Type in your name\n" +
                "> Unchanged results in \"${highscoreDefaultName}\"\n" +
                "> Press Enter to confirm\n" +
                "> Press Ctrl+C to escape without save"

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