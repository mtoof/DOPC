package wolt.dopc.dto

import jakarta.validation.constraints.*
/**
    * This data class is used to handle the request parameters from the query params and validate them.
    * @param venue_slug: The slug of the venue.
    * @param cart_value: The cart value of the user.
    * @param user_lat: The latitude of the user.
    * @param user_lon: The longitude of the user.
**/
data class DopcParamRequest(
    @field:NotBlank(message = "Venue slug is required")
    val venue_slug: String,

    @field:NotNull(message = "cart value is required")
    @field:Min(value = 1, message = "cart value must be > 0")
    val cart_value: Int,

    @field:NotNull(message = "User latitude is required")
    @field: DecimalMin(value = "-90.0", inclusive = true, message = "User Latitude must be >= -90.0")
    @field: DecimalMax(value = "90.0", inclusive= true, message = "User Latitude must be <= 90.0")
    val user_lat: Double,

    @field:NotNull(message = "User longitude is required")
    @field: DecimalMin(value = "-180.0", inclusive = true, message = "User Longitude must be >= -180.0")
    @field: DecimalMax(value = "180.0", inclusive= true, message = "User Longitude must be <= 180.0")
    val user_lon: Double
)

