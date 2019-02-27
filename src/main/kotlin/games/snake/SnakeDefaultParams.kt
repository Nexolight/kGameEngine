package games.snake

class SnakeDefaultParams {
    companion object {
        val mapwidth:Int = 50
        val mapheight:Int = 30

        /**
         * Welcome message and timer before start
         */
        val welcomeTimer:Int = 5;
        val welcomeMsg:String = "Starting ksnake in:\n "

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
         * By how much the player controlled snake
         * is feed at the beginning
         */
        val initialFeed:Int = 6

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