package games.snake

import abstracted.Entity
import abstracted.LogicCompositor
import flow.ActionHandler
import abstracted.entity.presets.Align
import abstracted.entity.presets.TextEntity
import abstracted.entity.presets.TextPairEntity
import abstracted.ui.`if`.ASCIISupport
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.Pool
import games.snake.entitylogic.GameOverLogic
import games.snake.entitylogic.PlayerLogic
import games.snake.entitylogic.entities.EdibleEntity
import games.snake.entitylogic.entities.SnakeEntity
import games.snake.entitylogic.entities.WallEntity
import games.snake.helper.RuntimeDispValD
import games.snake.helper.RuntimeDispValI
import games.snake.helper.RuntimeDispValS
import models.*
import java.text.DecimalFormat
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.roundToInt

class SnakeGameLogic : LogicCompositor{

    var firstAction:Boolean = true
    val welcome: TextEntity = TextEntity(Position(2, 10, 0))
    val gameOver: TextEntity = TextEntity(Position(2, 10, 0))
    override var field: Field = Field(SnakeDefaultParams.mapwidth,SnakeDefaultParams.mapheight)
    private val notifyQueue: ConcurrentLinkedQueue<Notification> = ConcurrentLinkedQueue<Notification>()
    var playerLogic:PlayerLogic? = null
    var gameOverLogic:GameOverLogic? = null
    var snakeFood:EdibleEntity? = null
    var startTime:Long = System.currentTimeMillis()
    var dispValues: TextPairEntity = TextPairEntity(
            Position(SnakeDefaultParams.mapwidth+1,0,0),
            Rotation(0.0,0.0,0.0),
            30,1,'+'
    )
    var tickSpeed:Double = SnakeDefaultParams.tickSpeed.toDouble()
    var gameOverCheck:Boolean = false
    var gameOverTime:Long = 0L

    /**
     * Updated and displayed game values
     */
    val playtime:RuntimeDispValI = RuntimeDispValI(
            0, "Playtime (s)", 0
    )
    var playerMultiplier: RuntimeDispValD = RuntimeDispValD(
            1,"Speed multiplier:", 1.0
    )
    val playerFood:RuntimeDispValI = RuntimeDispValI(
            2,"Food eaten: ",0
    )
    val playerPoints:RuntimeDispValI = RuntimeDispValI(
            3,"Score:", 0
    )
    val playerState:RuntimeDispValS = RuntimeDispValS(
            4,"Snake is:", "ALIVE"
    )


    var lastBuffSpawn:Long = System.currentTimeMillis()

    constructor(ah:ActionHandler,kryoPool: Pool<Kryo>):super(ah,kryoPool){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
        ah.subscribeNotification(Notification(this,NotificationType.COLLISION))
        ah.subscribeNotification(Notification(this,NotificationType.GAMESIGNAL))

    }

    override fun requestAction() {
        val now:Long = System.currentTimeMillis()
        val elements = field.entities.size
        var dispValuesUpdated:Boolean = false

        //TODO: ingame menu debug log options
        if(super.actionRequestTicks % 6 == 0.toLong()){
            igLogI("ActionRequests: ${super.actionRequestTicks} elements: $elements")
        }


        /**
         * Welcome sequence
         */
        if(firstAction){
            if(super.actionRequestTicks == 0.toLong()){
                super.igLogI("Loading game")
                genMap()
                field.entities.add(welcome)
            }
            if(super.actionRequestTicks < SnakeDefaultParams.welcomeTimer){
                super.addActionRequestDelay(1000)
                welcome.updateText(
                        "${SnakeDefaultParams.welcomeMsg}${SnakeDefaultParams.welcomeTimer-super.actionRequestTicks}",
                        SnakeDefaultParams.mapwidth-4,
                        Align.CENTER)
            }else{
                field.entities.remove(welcome)
                genSnake()
                initDispValues()
                firstAction = false
            }
        }else{
            //switch to normal tick speed
            super.addActionRequestDelay(tickSpeed.toLong())
        }


        /**
         * GameOver sequcence
         */
        if(gameOverTime != 0L){
            if(gameOverCheck){//action right after the playerLogic died - once
                gameOverCheck=false
                gameOver.updateText(
                        "${SnakeDefaultParams.gameOverMsg}${SnakeDefaultParams.gameOverTimer}",
                        SnakeDefaultParams.mapwidth-4,
                        Align.CENTER)
                field.entities.add(gameOver)
                despawnEdibles()
                playerLogic?.kill=true
                super.addActionRequestDelay(1000)//override
            }
            val tDelta:Long = System.currentTimeMillis() - gameOverTime
            if(tDelta >= SnakeDefaultParams.gameOverTimer*1000){
                field.entities.remove(gameOver)
                doGameOver()
                gameOverTime=0L//double usage for timer end game over sequence
            }else{
                gameOver.updateText(
                        "${SnakeDefaultParams.gameOverMsg}${SnakeDefaultParams.gameOverTimer - (tDelta / 1000).toInt()}",
                        SnakeDefaultParams.mapwidth-4,
                        Align.CENTER)
                super.addActionRequestDelay(1000)//override
            }
        }

        /**
        * Playtime update. only do it as long as the playerLogic is active
         */
        if(playerLogic != null && playerLogic?.isAlive == true && now - startTime > 1000){
            playtime.value = ((now-startTime)/1000).toInt()
            dispValuesUpdated=true
            dispValues.setPair(playtime.row,playtime.name,playtime.value.toString(),false)
        }

        /**
         * Game related async/sub functions
         */
        if(playerLogic != null && playerLogic?.isAlive == true){
            playerLogic?.actionRequest()//ASYNC
            spawnEdible()//SYNC
        }

        if(gameOverLogic != null && gameOverLogic?.isAlive == true){
            gameOverLogic?.actionRequest()//ASYNC
        }

        /**
         * Process the notifications in game thread (SYNC)
         */
        while(notifyQueue.size>0) {
            val n: Notification = notifyQueue.poll()

            /**
             * Update displayed values.
             * The individual logics will notfy us about the
             * stuff we want to have displayed
             */
            if(n.type == NotificationType.GAMESIGNAL && n.pair != null){
                when(n.pair.first){
                    SnakeGameSignals.snakeEat ->{
                        if(n.pair.second is Int){
                            playerFood.value += n.pair.second
                            playerPoints.value += (
                                    SnakeDefaultParams.pointsPerFood * playerMultiplier.value
                                    ).roundToInt()
                        }
                        dispValuesUpdated=true
                        dispValues.setPair(playerPoints.row,playerPoints.name,playerPoints.value.toString(),false)
                        dispValues.setPair(playerFood.row,playerFood.name,playerFood.value.toString(),false)
                    }
                    SnakeGameSignals.newBuffValue->{
                        if(n.pair.second is Double){
                            playerMultiplier.value=playerMultiplier.value*n.pair.second
                            tickSpeed=tickSpeed*(1/n.pair.second)
                        }
                        dispValuesUpdated=true
                        dispValues.setPair(playerMultiplier.row, playerMultiplier.name, DecimalFormat("#.###").format(playerMultiplier.value).toString(),false)
                    }
                    SnakeGameSignals.playerDeath->{
                        dispValuesUpdated=true
                        gameOverCheck=true
                        gameOverTime=System.currentTimeMillis()
                        dispValues.setPair(playerState.row,playerState.name,"DEAD",false)
                    }
                }

            }
        }

        /**
         * Update the displayed values at once if changed
         */
        if(dispValuesUpdated==true){
            dispValues.updatePairs()
        }

        /**
         * TODO:
         * at this point updates need to be finished
         * otherwise it will mess up the serialization
         */
        while(
                (playerLogic != null && playerLogic?.isAlive == true && playerLogic?.actionRequestPending() == true) ||
                (gameOverLogic != null && gameOverLogic?.isAlive == true && gameOverLogic?.actionRequestPending() == true)
                ){
            Thread.sleep(1)
        }

    }

    /**
     * Initially add the display value pairs
     */
    fun initDispValues(){
        field.entities.add(dispValues)
        dispValues.setPair(playerPoints.row,playerPoints.name,playerPoints.value.toString(),false)
        dispValues.setPair(playerFood.row,playerFood.name,playerFood.value.toString(),false)
        dispValues.setPair(playerMultiplier.row, playerMultiplier.name, DecimalFormat("#.###").format(playerMultiplier.value).toString(),false)
        dispValues.setPair(playtime.row,playtime.name,playtime.value.toString(),false)
        dispValues.setPair(playerState.row,playerState.name,playerState.value,false)
        dispValues.updatePairs()
    }


    /**
     * Removes all edibles from the game field
     */
    fun despawnEdibles(){
        val edibles:ArrayList<EdibleEntity> = ArrayList<EdibleEntity>()
        for(e:Entity in field.entities){
            if(e is EdibleEntity){
                edibles.add(e)
            }
        }
        field.entities.removeAll(edibles)
    }

    /**
     * Will spawn food and buffs
     */
    fun spawnEdible(){
        if(snakeFood == null || snakeFood?.wasEaten == true){
            val food:EdibleEntity = EdibleEntity(getFreePos())
            food.buffs.add(SnakeBuffs.food)
            snakeFood = food
            field.entities.add(snakeFood)
        }
        if(System.currentTimeMillis() >= SnakeDefaultParams.buffSpawnIntervall+lastBuffSpawn){
            val buff:EdibleEntity = EdibleEntity(getFreePos())
            val rand:Int = (0..3).shuffled().last()
            when(rand){//random buff
                0->buff.buffs.add(SnakeBuffs.speedupM)
                1->buff.buffs.add(SnakeBuffs.speedupL)
                2->buff.buffs.add(SnakeBuffs.speeddownM)
                3->buff.buffs.add(SnakeBuffs.speeddownL)
            }
            lastBuffSpawn=System.currentTimeMillis()
            field.entities.add(buff)
        }
    }

    /**
     * Returns a position that is not visually blocked.
     * TODO: this is 2d only. should be 3d
     */
    fun getFreePos():Position{
        val freePos:ArrayList<Position>  = ArrayList<Position>()
        for(x in 1..(SnakeDefaultParams.mapwidth-2)){
            for(y in 1..(SnakeDefaultParams.mapheight-2)){
                freePos.add(Position(x,y,0))
            }
        }
        for(e:Entity in field.entities){
            if(e is ASCIISupport){//TODO: possible since the game support ASCII but ugly
                freePos.minus(e.occupies())
            }
        }
        freePos.shuffle()
        return freePos.get(0)
    }

    /**
     * Snake generation
     */
    fun genSnake(){
        val newSnake = SnakeEntity(
                Position(SnakeDefaultParams.mapwidth/2,SnakeDefaultParams.mapheight/2,0),
                Rotation(0.0,0.0,0.0)
        )
        val newPlayer:PlayerLogic = PlayerLogic(field, newSnake, ah)
        newPlayer.start()
        playerLogic = newPlayer
    }

    fun doGameOver(){
        val highscore:TextPairEntity = TextPairEntity(
                Position(2,2,0),
                Rotation(0.0,0.0,0.0),
                SnakeDefaultParams.mapwidth-4,0,null
        )
        val gameOver:GameOverLogic = GameOverLogic(
                field,highscore,
                ah,
                HighScoreVals(SnakeDefaultParams.highscoreDefaultName,
                        playerPoints.value.toLong(),
                        playtime.value.toLong()))
        gameOver.start()
        gameOverLogic = gameOver
    }

    /**
     * Map generation
     */
    fun genMap(){
        field.entities.add(
            WallEntity(
                    Position(0,0,0),
                    Size(SnakeDefaultParams.mapwidth,1,1),
                    Rotation(0.0,0.0,0.0))
        )
        field.entities.add(
            WallEntity(
                    Position(0,SnakeDefaultParams.mapheight-1,0),
                    Size(SnakeDefaultParams.mapwidth,1,1),
                    Rotation(0.0,0.0,0.0))
        )
        field.entities.add(
            WallEntity(
                    Position(0,1,0),
                    Size(1,SnakeDefaultParams.mapheight-2,1),
                    Rotation(0.0,0.0,0.0))
        )
        field.entities.add(
            WallEntity(
                    Position(SnakeDefaultParams.mapwidth-1,1,0),
                    Size(1,SnakeDefaultParams.mapheight-2,1),
                    Rotation(0.0,0.0,0.0))
        )
    }

    override fun onLCNotify(n: Notification) {
        if(n.type == NotificationType.USERINPUT) {
            igLogI("User input: ${n.chr} (${n.chr.toInt()})")
            return
        }else{//outsource
            notifyQueue.add(n)
        }
    }

}