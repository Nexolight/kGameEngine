package models

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class for saving, retrieving and store the entities.HighScore
 */
class HighScore(){
    companion object {
        /**
         * Loads the highscore from the given path
         */
        fun load(path: Path):HighScore{
            if(Files.notExists(path)){
                return HighScore()
            }else if(Files.readAllBytes(path).size == 0){
                return HighScore()
            }
            val gson:Gson = GsonBuilder().create()
            val infile = FileReader(path.toString())
            val highscore:HighScore = gson.fromJson(infile,HighScore::class.java)
            infile.close()
            return highscore
        }

        /**
         * Stores/overrides the highscore to the given path
         */
        fun persist(path:Path,highscore:HighScore){
            if(Files.notExists(path.parent)){
                Files.createDirectories(path.parent)
            }
            val gson:Gson = GsonBuilder().create()
            val outfile = FileWriter(path.toString())
            gson.toJson(highscore,HighScore::class.java,outfile)
            outfile.flush()
            outfile.close()
        }
    }

    val entries:ArrayList<HighScoreVals> = ArrayList<HighScoreVals>()


    /**
     * Inserts a new entry into the highscore,
     * replaces the entry if one with that name
     * already exists
     */
    fun insert(newEntry:HighScoreVals){
        var index:Int = -1
        for(e:IndexedValue<HighScoreVals> in entries.withIndex()){
            if(e.value.name==newEntry.name){
                index=e.index
                break
            }
        }
        if(index == -1){
            entries.add(newEntry)
        }else{
            entries[index] = newEntry
        }
    }

    /**
     * Sort the list by score. Higher values first
     */
    fun sort(){
        Collections.sort(entries, { o1, o2 -> o1.score.compareTo(o2.score) })
        entries.reverse()
    }

    /**
     * Removes entries and only leaves the specified amount on top
     */
    fun limit(max:Int){
        if(entries.size<=max){
            return
        }
        entries.removeAll(entries.subList(max,entries.size))
    }

}

/**
 * High score values. Player name is supposed to be unique
 */
class HighScoreVals(val name:String, val score:Long, val playtime:Long){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HighScoreVals

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}