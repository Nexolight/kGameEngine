package models

/**
 * Position element with 3 dimensions
 */
class Size(){
    var width:Double = 0.0
    var height:Double = 0.0
    var depth:Double = 0.0

    /**
     * Initialize by multiplying with base units
     */
    constructor(widthBaseUnits:Int=0,heightBaseUnits:Int=0,depthBaseUnits:Int=0):this(){
        this.width=widthBaseUnits*BaseUnits.ONE
        this.height=heightBaseUnits*BaseUnits.ONE
        this.depth=depthBaseUnits*BaseUnits.ONE
    }

    /**
     * Initialize by multiplying with base units
     */
    constructor(widthBaseUnits:Double=0.0,heightBaseUnits:Double=0.0,depthBaseUnits:Double=0.0):this(){
        this.width=widthBaseUnits
        this.height=heightBaseUnits
        this.depth=depthBaseUnits
    }

    /**
     * Intended for panes, ensures the thinnest
     * still rendering depth size component.
     */
    fun makeFlat():Size{
        this.depth = BaseUnits.FLAT
        return this
    }

    /**
     * copy paste values
     */
    fun pasteFrom(size:Size):Size{
        width=size.width
        height=size.height
        depth=size.depth
        return this
    }


    /**
     * If any of the values is not null this
     * returns true
     */
    fun notNull():Boolean{
        if(width != 0.0 || height != 0.0 || depth != 0.0){
            return true
        }
        return false
    }

    /**
     * Apply size changes
     */
    fun transform(transform:Size){
        width+=transform.width
        height+=transform.height
        depth+=transform.depth
    }

    /**
     * width size change in base units
     */
    fun transformWidth(baseUnits:Int=0){
        width+=baseUnits*BaseUnits.ONE
    }

    /**
     * height size change in base units
     */
    fun transformHeight(baseUnits:Int=0){
        height+=baseUnits*BaseUnits.ONE
    }

    /**
     * depth size change in base units
     */
    fun transformDepth(baseUnits:Int=0){
        depth+=baseUnits*BaseUnits.ONE
    }

    override fun toString():String{
        return "Size: width: $width, height: $height, depth: $depth"
    }
}