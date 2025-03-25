package wolt.dopc.service

import org.springframework.stereotype.Service
import wolt.dopc.Exeption.InvalidDistanceException
import wolt.dopc.client.ApiClient
import wolt.dopc.dto.*
import kotlin.math.*

/**
 * This class is used to calculate the delivery order price.
 * @param apiClient: The ApiClient object.
**/
@Service
class DopcService(private val apiClient: ApiClient) {

    /**
     * This function will calculate the delivery order price based on the user's location and the cart value.
     * @param venueSlug: The venue slug.
     * @param cartValue: The cart value of the user.
     * @param userLat: The user's latitude.
     * @param userLon: The user's longitude.
     * @return DopcResponse: The response containing the total price, surcharge, cart value, and delivery details.
    **/
    fun deliveryOrderPriceCalculator(
        venueSlug: String, cartValue: Int, userLat: Double, userLon: Double,
    ): DopcResponse {

        val fee: Int

        val venueLocation = apiClient.fetchDataFromStaticAPI(venueSlug)
        val venueDetails = apiClient.fetchDataFromDynamicAPI(venueSlug)

        val distance: Double =
            (haversineFormula(userLat, userLon, venueLocation[0], venueLocation[1]) * 1000)

        val surcharge = cartValueSurcharge(cartValue, venueDetails)
        val rangeIter: DistanceRange = rangeFinder(distance, venueDetails.deliveryPricing)
        fee = calculateFee(rangeIter, distance, venueDetails.deliveryPricing.basePrice)
        val totalFee: Int = cartValue + fee + surcharge

        val distanceObj = Delivery(fee, distance.roundToInt())
        return DopcResponse(totalFee, surcharge, cartValue, distanceObj)
    }


    /**
     * This function will calculate the distance between two points using the haversine formula.
     * @param userLat: The latitude of the user.
     * @param userLon: The longitude of the user.
     * @param venueLongitude: The longitude of the venue.
     * @param venueLatitude: The latitude of the venue.
     * @return Double: The distance between the two points.
    **/
    fun haversineFormula(
        userLat: Double,
        userLon: Double,
        venueLongitude: Double,
        venueLatitude: Double,
    ): Double {

        val earthRadius = 6371.0 //Earth radius in Kilometers

        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(venueLatitude)

        val lon1 = Math.toRadians(userLon)
        val lon2 = Math.toRadians(venueLongitude)

        val deltaLat = lat2 - lat1
        val deltaLon = lon2 - lon1

        val a = sin(deltaLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadius * c
        return distance
    }

    /**
     * This function will calculate the surcharge based on the cart value and the venue details.
     * @param cartValue: The cart value of the user.
     * @param venueDetails: The venue details containing the order minimum no surcharge.
     * @return Int: The surcharge amount.
    **/

    fun cartValueSurcharge(cartValue: Int, venueDetails: DeliverySpecs): Int {
        return if (cartValue < venueDetails.orderMinimumNoSurcharge)
            (venueDetails.orderMinimumNoSurcharge - cartValue)
        else 0
    }

    /**
     * This function will find the distance range based on the user's distance.
     * @param distance: The distance between the user and the venue.
     * @param deliveryPricing: The delivery pricing details.
     * @return DistanceRange: The distance range object.
    **/

    fun rangeFinder(distance: Double, deliveryPricing: DeliveryPricing): DistanceRange {
        val distanceRanges = deliveryPricing.distanceRanges.listIterator()

        for (currentDistanceRange in distanceRanges) {
            if (currentDistanceRange.max == 0) {
                // No delivery for distances equal to or beyond min range
                if (distance >= currentDistanceRange.min)
                    break
            } else if (distance >= currentDistanceRange.min && distance < currentDistanceRange.max) {
                return currentDistanceRange
            }
        }
        throw InvalidDistanceException("Sorry we can't deliver, you're outside of our delivery range")

    }

    /**
     * This function will calculate the delivery fee based on the distance and the delivery pricing details.
     * @param rangeIter: The distance range object.
     * @param distance: The distance between the user and the venue.
     * @param basePrice: The base price of the delivery.
     * @return Int: The delivery fee.
    **/
    fun calculateFee(rangeIter: DistanceRange, distance: Double, basePrice: Int): Int {
        //base_price + a + b * distance / 10.

        val fixedAmount = rangeIter.a
        val distanceMultiplier  = rangeIter.b
        return (basePrice + fixedAmount + distanceMultiplier * distance / 10).roundToInt()
    }
}