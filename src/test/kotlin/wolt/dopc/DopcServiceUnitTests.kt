package wolt.dopc

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import wolt.dopc.Exeption.InvalidDistanceException
import wolt.dopc.client.ApiClient
import wolt.dopc.dto.*
import wolt.dopc.service.DopcService
import kotlin.math.roundToInt
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class) // JUnit5 Extension for MockK to automatically initialize mocks
class DopcServiceUnitTests {

    @MockK
    lateinit var apiClient: ApiClient // Mocking the ApiClient HTTP Client Component
    private lateinit var service: DopcService
    private lateinit var venueSlug: String
    private lateinit var venueLocation: List<Double>
    private lateinit var deliverySpecs: DeliverySpecs

    @BeforeEach
    fun setUp() {
        service = DopcService(apiClient)

        venueSlug = "home-assignment-venue-helsinki"

        venueLocation = listOf(24.92813512, 60.17012143)
        deliverySpecs = DeliverySpecs(
            orderMinimumNoSurcharge = 1000, deliveryPricing = DeliveryPricing(
                basePrice = 190, distanceRanges = listOf(
                    DistanceRange(min = 0, max = 500, a = 0, b = 0, flag = false),
                    DistanceRange(min = 500, max = 1000, a = 100, b = 0, flag = false),
                    DistanceRange(min = 1000, max = 1500, a = 200, b = 0, flag = false),
                    DistanceRange(min = 1500, max = 2000, a = 200, b = 1, flag = false),
                    DistanceRange(min = 2000, max = 0, a = 0, b = 0, flag = false)
                )
            )
        )

        every { apiClient.fetchDataFromStaticAPI(venueSlug) } returns venueLocation
        every { apiClient.fetchDataFromDynamicAPI(venueSlug) } returns deliverySpecs

    }

    @Test
    fun `test service delivery order price calculator`() {
        val cartValue = 1000
        val userLat = 60.17094
        val userLon = 24.93087

        val actualResult = service.deliveryOrderPriceCalculator(
            venueSlug, cartValue, userLat, userLon
        )
        val expectedResult = DopcResponse(1190, 0, 1000, Delivery(190, 177))
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `test Service haversine formula`() {
        val userLat = 60.17094
        val userLon = 24.93087

        val distance = service.haversineFormula(
            userLat, userLon, venueLocation[0], venueLocation[1]
        )
        val expectedDistance = 177 //roundToInt distance
        val actualDistance = (distance * 1000).roundToInt()
        assertEquals(expectedDistance, actualDistance)
    }

    @Test
    fun `test service cart_value surcharge`() {
        val correctResult = service.cartValueSurcharge(1000, deliverySpecs)
        val expectSurcharge = service.cartValueSurcharge(800, deliverySpecs)
        assertEquals(0, correctResult)
        assertEquals(200, expectSurcharge)
    }

    @Test
    fun `test service distance range finder`() {
        val result = service.rangeFinder(177.0, deliverySpecs.deliveryPricing)

        var expectedResult = deliverySpecs.deliveryPricing.distanceRanges[0]
        assertEquals(expectedResult, result)

        val shouldBeInRangeMoreThanOneThousand = service.rangeFinder(
            1000.0, deliverySpecs.deliveryPricing
        )

        expectedResult = deliverySpecs.deliveryPricing.distanceRanges[2]
        assertEquals(expectedResult, shouldBeInRangeMoreThanOneThousand)

    }

    @Test
    fun `should throw exception for out-of-range distance`() {
        //expected to throw Exception for a user outside the distance ranges
        val exceptionResult = assertThrows<InvalidDistanceException> {
            service.rangeFinder(2500.0, deliverySpecs.deliveryPricing)
        }
        assert(exceptionResult.message?.contains("Sorry we can't deliver, you're outside of our delivery range") == true)
    }

    @Test
    fun `test service calculate delivery fee`() {
        val userDistance = 200.0
        val baseFee = deliverySpecs.deliveryPricing.basePrice
        val distanceRange = deliverySpecs.deliveryPricing.distanceRanges[0]

        val result = service.calculateFee(
            distanceRange, userDistance, deliverySpecs.deliveryPricing.basePrice
        )

        assertEquals(baseFee, result)
    }
}