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
}