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
    private var playername:String = playerStats.name

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
                    //TODO: is ctrl+c appropriate here?
                    persistHighScore()
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

        //Override the same name
        val newHS:HighScoreVals = HighScoreVals(playername,playerStats.score,playerStats.playtime)
        val existingIndex:Int = persistentHS.entries.indexOf(newHS)
        if(existingIndex > -1){
            playerIndex=existingIndex

            //We don't override a lower score
            if(persistentHS.entries[playerIndex].score < playerStats.score){
                persistentHS.entries[playerIndex] = newHS
            }

            //This is a dummy entry, we don't need it when we override an existing one
            persistentHS.entries.removeAt(persistentHS.entries.indexOf(
                    HighScoreVals(SnakeDefaultParams.highscoreEntryMsg,playerStats.score,playerStats.playtime)))
        }else{
            persistentHS.entries[playerIndex] = newHS
        }


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
        var position:Int = -1
        persistentHS.entries.add(HighScoreVals(SnakeDefaultParams.highscoreEntryMsg,playerStats.score,playerStats.playtime))
        persistentHS.sort()
        persistentHS.limit(SnakeDefaultParams.highscoreLimit)
        for(entry:IndexedValue<HighScoreVals> in persistentHS.entries.withIndex()){
            highscore.setPair(entry.index,
                    getHSNameStr(entry.index+1,entry.value.name),
                    getHSValStr(entry.value.playtime,entry.value.score),
                    false)
            if(entry.value.name == SnakeDefaultParams.highscoreEntryMsg){
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
        var scorestr:String = "${score} pts"
        val delta:Int = 10 - scorestr.length
        scorestr=" ".repeat(delta).plus(scorestr)
        return "${playtime}s | ${scorestr}"
    }

    /**
     * Returns the name string for the displayed highscore
     */
    fun getHSNameStr(pos:Int, name:String):String{
        val delta:Int = 3-pos.toString().length
        val noStr:String = (pos).toString().plus(" ".repeat(delta))
        return "${noStr}| ${name}"
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

        //Score to low - ignore inputs
        if(playerIndex < 0){
            return
        }

        //Clear the default name, The user wrote something
        if(playername == SnakeDefaultParams.highscoreDefaultName){
            playername=""
        }

        //Backspace
        if((c == 8.toChar() || c == 127.toChar()) && playername.isNotEmpty()){
            playername = playername.substring(0,playername.length-1)
        }else{
            playername+=c
        }
        highscore.setPair(playerIndex,getHSNameStr(playerIndex+1,playername),
                getHSValStr(playerStats.playtime,playerStats.score),
                true)
    }


    /**
     * Process the received notifications
     */
    fun processNotifications(){
        while(notifyQueue.size>0){
            val n:Notification = notifyQueue.poll()
            //Store user input
            if(n.type == NotificationType.USERINPUT){
                procUIP(n.chr)
                continue
            }

        }
    }

    override fun onNotify(n: Notification) {
        //Kill on termination
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            ah.notify(Notification(this,NotificationType.INGAME_LOG_INFO,"Killing GameOverLogic!"))
            kill = true
            return
        }
        notifyQueue.add(n) //use the playerLogic logic thread later for processing
    }
}