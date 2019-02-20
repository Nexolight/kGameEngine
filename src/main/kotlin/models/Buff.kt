package models

/**
 * Buffs are a part of every entity and can be used to
 * add certain properties to one
 */
class Buff(
        val description:String = "",
        val buffID:Int = 0,
        val buffValue:Double = 0.0
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Buff

        if (buffID != other.buffID) return false

        return true
    }

    override fun hashCode(): Int {
        return buffID
    }
}