package wolt.dopc.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This data class is used to handle the response from the dynamic API.
**/
@JsonIgnoreProperties(ignoreUnknown = true)
data class DynamicApiResponse(
    @JsonProperty("venue_raw") val venueRaw: DynamicVenueRaw
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DynamicVenueRaw(
    @JsonProperty("delivery_specs") val deliverySpecs: DeliverySpecs
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliverySpecs(
    @JsonProperty("order_minimum_no_surcharge") val orderMinimumNoSurcharge: Int,
    @JsonProperty("delivery_pricing") val deliveryPricing: DeliveryPricing
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryPricing(
    @JsonProperty("base_price") val basePrice: Int,
    @JsonProperty("distance_ranges") val distanceRanges: List<DistanceRange>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DistanceRange(
    @JsonProperty("min") val min: Int,
    @JsonProperty("max") val max: Int,
    @JsonProperty("a") val a: Int,
    @JsonProperty("b") val b: Int,
    @JsonProperty("flag") val flag: Boolean

)