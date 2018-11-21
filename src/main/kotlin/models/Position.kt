package models

/**
 * Position element with 3 dimensions
 */
class Position{
    var x:Double
    var y:Double
    var z:Double
    constructor(x:Int=0,y:Int=0,z:Int=0){
        this.x=x*BaseUnits.ONE
        this.y=y*BaseUnits.ONE
        this.z=z*BaseUnits.ONE
    }
}