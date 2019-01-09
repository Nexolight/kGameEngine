package models

/**
 * Position element with 3 dimensions
 */
class Position{
    var x:Double
    var y:Double
    var z:Double
    constructor(xBaseUnits:Int=0,yBaseUnits:Int=0,zBaseUnits:Int=0){
        this.x=xBaseUnits*BaseUnits.ONE
        this.y=yBaseUnits*BaseUnits.ONE
        this.z=zBaseUnits*BaseUnits.ONE
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
}