package wolt.dopc.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This data class is used to handle the response from the static API.
**/
@JsonIgnoreProperties(ignoreUnknown = true)
data class StaticApiResponse(
    @JsonProperty("venue_raw") val venueRaw: StaticVenueRaw
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StaticVenueRaw(
    @JsonProperty("location") val location: Location
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    @JsonProperty("coordinates") val coordinates: List<Double>
)
