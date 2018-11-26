package abstracted.ui.`if`

import models.Position

/**
 * Interface should be implemented by every
 * entity that want's to support the
 * ASCII Compositor
 */
interface ASCIISupport {
    /**
     * Returns the Ascii character places
     * at the specific offset of the entity
     */
    fun getOccupyRepresentation(pos:Position):Char
}