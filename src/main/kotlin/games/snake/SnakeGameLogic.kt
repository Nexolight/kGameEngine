package games.snake

import abstracted.Entity
import abstracted.LogicCompositor
import flow.ActionHandler
import abstracted.entity.presets.Align
import abstracted.entity.presets.TextEntity
import abstracted.ui.`if`.ASCIISupport
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.Pool
import games.snake.entitylogic.PlayerLogic
import games.snake.entitylogic.entities.EdibleEntity
import games.snake.entitylogic.entities.SnakeEntity
import games.snake.entitylogic.entities.WallEntity
import models.*
import java.util.concurrent.ConcurrentLinkedQueue

class SnakeGameLogic : LogicCompositor{

    var firstAction:Boolean = true
    val welcome: TextEntity = TextEntity(Position(2, 10, 0))
    override var field: Field = Field(SnakeDefaultParams.mapwidth,SnakeDefaultParams.mapheight)
    private val notifyQueue: ConcurrentLinkedQueue<Notification> = ConcurrentLinkedQueue<Notification>()
    var player:PlayerLogic? = null
    var snakeFood:EdibleEntity? = null
    var lastBuffSpawn:Long = System.currentTimeMillis()

    constructor(ah:ActionHandler,kryoPool: Pool<Kryo>):super(ah,kryoPool){
        ah.subscribeNotification(Notification(this,NotificationType.USERINPUT))
        ah.subscribeNotification(Notification(this,NotificationType.COLLISION))
    }

    override fun requestAction() {
        val elements = field.entities.size

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
                        "${SnakeDefaultParams.welcomeMsg}\n${5-super.actionRequestTicks}",
                        SnakeDefaultParams.mapwidth-4,
                        Align.CENTER)
            }else{
                field.entities.remove(welcome)
                genSnake()
                firstAction = false
            }
        }else{
            //TODO: lower this, make the delay individual for entity logics
            super.addActionRequestDelay(SnakeDefaultParams.tickSpeed)
        }

        /**
         * Process some notifications in game thread
         *
        while(notifyQueue.size>0) {
            val n: Notification = notifyQueue.poll()
        }*/


        if(player != null){
            player?.actionRequest()

            spawnEdible()

            /**
             * TODO:
             * at this point updates need to be finished
             * otherwise it will mess up the serialization
             */
            while(player?.actionRequestPending() == true){
                Thread.sleep(1)
            }

        }
    }

    /**
     * Will spawn food and buffs
     * when none of these is on the field
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
            when(rand){
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
            if(e is ASCIISupport){
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
        field.entities.add(newPlayer.snake)
        newPlayer.start()
        player = newPlayer
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
            igLogI("User input: ${n.chr}")
            return
        }else{//outsource
            notifyQueue.add(n)
        }
    }

}