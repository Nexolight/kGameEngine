package models

/**
 * Position element with 3 dimensions
 */
class Position(){
    var x:Double = 0.0
    var y:Double = 0.0
    var z:Double = 0.0

    /**
     * Initialize by multiplying with base units
     */
    constructor(xBaseUnits:Int=0,yBaseUnits:Int=0,zBaseUnits:Int=0) : this(){
        this.x=xBaseUnits*BaseUnits.ONE
        this.y=yBaseUnits*BaseUnits.ONE
        this.z=zBaseUnits*BaseUnits.ONE
    }

    /**
     * Initialize by multiplying with base units
     */
    constructor(xBaseUnits:Double=0.0,yBaseUnits:Double=0.0,zBaseUnits:Double=0.0) : this(){
        this.x=xBaseUnits*BaseUnits.ONE
        this.y=yBaseUnits*BaseUnits.ONE
        this.z=zBaseUnits*BaseUnits.ONE
    }

    /**
     * copy paste values
     */
    fun pasteFrom(pos:Position):Position{
        x=pos.x
        y=pos.y
        z=pos.z
        return this
    }

    /**
     * If any of the values is not null this
     * returns true
     */
    fun notNull():Boolean{
        if(x != 0.0 || y != 0.0 || z != 0.0){
            return true
        }
        return false
    }

    /**
     * Apply position changes
     */
    fun transform(transform:Position){
        x+=transform.x
        y+=transform.y
        z+=transform.z
    }

    /**
     * x position change in base units
     */
    fun transformX(baseUnits:Int=0){
        x+=baseUnits*BaseUnits.ONE
    }

    /**
     * y position change in base units
     */
    fun transformY(baseUnits:Int=0){
        y+=baseUnits*BaseUnits.ONE
    }

    /**
     * z position change in base units
     */
    fun transformZ(baseUnits:Int=0){
        z+=baseUnits*BaseUnits.ONE
    }

    override fun toString():String{
        return "Position: x: $x, y: $y, z: $z"
    }

}