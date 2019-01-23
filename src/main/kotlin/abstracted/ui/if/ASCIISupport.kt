package abstracted.ui.`if`

import models.AdvancedQube
import models.Position
import models.Rotation

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
    fun getOccupyRepresentation(pos:Position,rota:Rotation):Char

    /**
     * Returns a simplified list with blocks
     * that the entity occupies
     */
    fun occupies():List<AdvancedQube>

}