package models

/**
 * Rotation element with 3 dimensions
 */
class Rotation(
        var xAxis:Double = 0.0,
        var yAxis:Double = 0.0,
        var zAxis:Double = 0.0
) {

    /**
     * Apply rotation changes
     */
    fun transform(transform:Rotation){
        xAxis+=transform.xAxis
        yAxis+=transform.yAxis
        zAxis+=transform.zAxis
    }

    /**
     * copy paste values
     */
    fun pasteFrom(rota:Rotation):Rotation{
        xAxis=rota.xAxis
        yAxis=rota.yAxis
        zAxis=rota.zAxis
        return this
    }

    /**
     * If any of the values is not null this
     * returns true
     */
    fun notNull():Boolean{
        if(xAxis != 0.0 || yAxis != 0.0 || zAxis != 0.0){
            return true
        }
        return false
    }

    /**
     * x axis change
     */
    fun transformX(xAxis:Double){
        this.xAxis = xAxis
    }

    /**
     * y axis change
     */
    fun transformY(yAxis:Double){
        this.yAxis = yAxis
    }

    /**
     * z axis change
     */
    fun transformZ(zAxis:Double){
        this.zAxis = zAxis
    }

    override fun toString():String{
        return "Rotation: x: $xAxis, y: $yAxis, z: $zAxis"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rotation

        if (xAxis != other.xAxis) return false
        if (yAxis != other.yAxis) return false
        if (zAxis != other.zAxis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = xAxis.hashCode()
        result = 31 * result + yAxis.hashCode()
        result = 31 * result + zAxis.hashCode()
        return result
    }
}