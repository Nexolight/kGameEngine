package games.snake.entitylogic

import abstracted.entity.presets.TextPairEntity
import abstracted.logic.EntityLogic
import flow.ActionHandler
import games.snake.SnakeDefaultParams
import models.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The game over logic
 * Deals with the HighScore after the game ended
 */
class GameOverLogic(val field:Field, val highscore: TextPairEntity, val ah:ActionHandler, val playerStats:HighScoreVals): EntityLogic() {
    var kill:Boolean = false
    private val notifyQueue: ConcurrentLinkedQueue<Notification> = ConcurrentLinkedQueue<Notification>()
    private var persistentHS:HighScore = HighScore()
    private var playerIndex:Int = -1
    private var entryDone:Boolean = false

    /**
     * The livetime of this thread is supposed to be
     * from the moment of the game over screen until the user
     * typed in it's name and confirmed it.
     */
    override fun run(){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
        ah.subscribeNotification(Notification(this,NotificationType.SIGNAL))
        ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Initialize HighScore!"))

        //Add our highscore to the field

        playerIndex = populateHighScore()
        field.entities.add(highscore)

        while(!kill){
            if(super.actionRequestPending()){//process the last Action request
                processNotifications()
                if(entryDone){
                    persistHighScore()
                    //TODO: is ctrl+c appropriate here?
                    ah.notify(Notification(this,NotificationType.SIGNAL,2))
                    kill=true
                }
                super.actionRequestDone()
            }else{
                Thread.sleep(1)
            }
        }


        //Remove our highscore from the field
        field.entities.remove(highscore)

        //clear AR before exit
        if(super.actionRequestPending()){
            super.actionRequestDone()
        }
    }

    /**
     * Write back the altered entry
     * and persist
     */
    fun persistHighScore(){
        if(playerIndex < 0){
            return
        }
        persistentHS.entries[playerIndex] = HighScoreVals(playerStats.name,playerStats.score,playerStats.playtime)
        HighScore.persist(SnakeDefaultParams.highScorePath,persistentHS)
    }

    /**
     * Load the HighScore and adds the playerLogic
     * to it when he got enough points.
     *
     * Returns the position within the highscore or -1
     */
    fun populateHighScore():Int{
        persistentHS = HighScore.load(SnakeDefaultParams.highScorePath)
        persistentHS.insert(HighScoreVals("bla",5000L,60L))
        persistentHS.insert(HighScoreVals("klei",4500L,50L))
        persistentHS.insert(HighScoreVals("oreo",6000L,120L))


        val tmpName:String = "> [Type your name]..."
        var position:Int = -1
        persistentHS.insert(HighScoreVals(tmpName,playerStats.score,playerStats.playtime))
        persistentHS.sort()
        for(entry:IndexedValue<HighScoreVals> in persistentHS.entries.withIndex()){
            highscore.setPair(entry.index,
                    entry.value.name,
                    getHSValStr(entry.value.playtime,entry.value.score),
                    false)
            if(entry.value.name == "tmpName"){
                position=entry.index
            }
        }
        //persistentHS.limit(10)
        highscore.updatePairs()
        return position
    }

    /**
     * Returns the value string for the displayed highscore
     */
    fun getHSValStr(playtime:Long,score:Long):String{
        return "${playtime} s | ${score} pts"
    }

    /**
     * Process the user input
     * (writing the name on the highscore if possible)
     */
    fun procUIP(c:Char){

        if(entryDone){
            return
        }
        if(c == 10.toChar() || c == 13.toChar()){//Return
            entryDone=true
            ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Highscore entry phase ended"))
            return
        }

        if(playerIndex < 0){
            return
        }

        var newStr:String = playerStats.name

        //Backspace
        if(c == 8.toChar() && newStr.isNotEmpty()){
            newStr = newStr.substring(0,newStr.length-1)
        }else{
            newStr+=c
        }
        highscore.setPair(playerIndex,newStr,
                getHSValStr(playerStats.playtime,playerStats.playtime),
                false)
    }


    fun processNotifications(){
        while(notifyQueue.size>0){
            val n:Notification = notifyQueue.poll()
            //Store user input
            if(n.type == NotificationType.USERINPUT){
                procUIP(n.chr)
                continue
            }

            //Kill on termination
            if(n.type == NotificationType.SIGNAL && n.n == 2){
                ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Killing GameOverLogic!"))
                kill = true
                return
            }
        }
    }

    override fun onNotify(n: Notification) {
        notifyQueue.add(n) //use the playerLogic logic thread later for processing
    }
}