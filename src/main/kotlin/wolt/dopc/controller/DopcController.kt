package wolt.dopc.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wolt.dopc.dto.DopcParamRequest
import wolt.dopc.dto.DopcResponse
import wolt.dopc.service.DopcService

/**
 * This class is used to handle the API requests and responses.
 * @param dopcService: The service class that will handle the business logic.
**/
@RestController
@RequestMapping("/api/v1")
class DopcController(private val dopcService: DopcService) {

    /**
     * This function will calculate the delivery order price based on the user's location and the cart value.
     * @param requestParams: The request parameters containing the venue slug, cart value, user latitude, and user longitude.
     * @return ResponseEntity<Any>: The response entity containing the delivery order price.
    **/
    @GetMapping("/delivery-order-price")
    fun deliveryOrderPrice(@Valid requestParams: DopcParamRequest): ResponseEntity<DopcResponse> {

        val result = dopcService.deliveryOrderPriceCalculator(
            requestParams.venue_slug,
            requestParams.cart_value,
            requestParams.user_lat,
            requestParams.user_lon
        )
        return ResponseEntity.ok(result)
    }
}