package wolt.dopc.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min

/**
 * This data class is used to handle the final DOPC response API.
 * @param totalPrice: The total price of the delivery order.
 * @param smallOrderSurcharge: The small order surcharge.
 * @param cartValue: The cart value of the user.
 * @param delivery: The delivery details is an object containing the fee and distance.
**/

data class DopcResponse(
    @JsonProperty("total_price") val totalPrice: Int,
    @field:Min(0, message = "small_order_surcharge >= 0")
    @JsonProperty("small_order_surcharge") val smallOrderSurcharge: Int,
    @JsonProperty("cart_value") val cartValue: Int,
    @JsonProperty("delivery") val delivery: Delivery,
)

data class Delivery(
    @JsonProperty("fee") val fee: Int,
    @JsonProperty("distance") val distance: Int
)