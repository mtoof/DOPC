package wolt.dopc.dto

/**
 * This data class is used by InvalidDistanceExeption when the distance is out of the venue distance range.
 * @param message: The message to be displayed.
**/
data class InvalidDistanceResponse(
    val message: String?
)
