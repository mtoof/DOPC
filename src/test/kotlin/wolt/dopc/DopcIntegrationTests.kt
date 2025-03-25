package wolt.dopc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import wolt.dopc.dto.Delivery
import wolt.dopc.dto.DopcResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DopcIntegrationTests {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var restTemplate: TestRestTemplate
    var venueSlug: String = "home-assignment-venue-helsinki"
    var cartValue: Int = 0
    var userLat: Double = 0.0
    var userLon: Double = 0.0

    @BeforeEach
    fun setUpUserInRange1000m() {
        cartValue = 1000
        userLat = 60.17094
        userLon = 24.93087
    }

    fun setUpUserInRangeMoreThan2000m() {
        cartValue = 1000
        userLat = 60.17094
        userLon = 25.93087
    }

    private fun buildUrl(venueSlug: String, cartValue: Int?, userLat: Double, userLon: Double): String {
        return "/api/v1/delivery-order-price?venue_slug=$venueSlug&cart_value=$cartValue&user_lat=$userLat&user_lon=$userLon"
    }


    @Test
    fun `should return correct result`() {

        var url: String = buildUrl(venueSlug, cartValue, userLat, userLon)
        val response = restTemplate.getForEntity(url, String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode, "Expected HTTP 200 OK")
        assertNotNull(response.body, "Response should not be null")
        var actualResponse: DopcResponse = objectMapper.readValue(response.body!!)
        val expectedResult = DopcResponse(1190, 0, cartValue, Delivery(190, 177))
        assertEquals(expectedResult, actualResponse)

        url = buildUrl(venueSlug, 2000, userLat, 24.9394)
        val secondResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.OK, secondResponse.statusCode, "Expected HTTP 200 OK")
        assertNotNull(secondResponse.body, "Response should not be null")
        actualResponse = objectMapper.readValue(secondResponse.body!!)

        val secondExpectedResult = DopcResponse(2290, 0, 2000, Delivery(290, 630))
        assertEquals(secondExpectedResult, actualResponse)
    }

    @Test
    fun `should return error for invalid venue slug`() {
        //Check Invalid data for Empty venue_slug param
        var url: String = buildUrl("", cartValue, userLat, userLon)
        val venueSlugResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, venueSlugResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        var venueSlugDocumentContext = JsonPath.parse(venueSlugResponse.body)
        var venueSlugMessage = venueSlugDocumentContext.read<String>("$.venue_slug")
        assertEquals("Venue slug is required", venueSlugMessage)

        //Check Invalid data for Invalid venue_slug param
        url = buildUrl("wrong_venue_slug", cartValue, userLat, userLon)
        val venueSlugSecondResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, venueSlugSecondResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        venueSlugDocumentContext = JsonPath.parse(venueSlugSecondResponse.body)
        venueSlugMessage = venueSlugDocumentContext.read("$.message")
        assertEquals("No venue with slug of 'wrong_venue_slug' was found", venueSlugMessage)
    }

    @Test
    fun `should return error for invalid cart_value`() {
        //Check Invalid data for cart_value param is zero
        val url: String = buildUrl(venueSlug, 0, userLat, userLon)
        val cartValueResponse = restTemplate.getForEntity(url, String::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, cartValueResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        val cartValueDocumentContext = JsonPath.parse(cartValueResponse.body)
        val cartValueResponseMessage = cartValueDocumentContext.read<String>("$.cart_value")
        assertEquals("cart value must be > 0", cartValueResponseMessage)
    }

    @Test
    fun `should return error for invalid user latitude`() {
        //Check Invalid data for user_lat param
        var url: String = buildUrl(venueSlug, cartValue, -95.0, userLon)
        val userLatitudeResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, userLatitudeResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        var userLatitudeDocumentContext = JsonPath.parse(userLatitudeResponse.body)
        var userLatitudeResponseMessage = userLatitudeDocumentContext.read<String>("$.user_lat")
        assertEquals("User Latitude must be >= -90.0", userLatitudeResponseMessage)

        url = buildUrl(venueSlug, cartValue, 95.0, userLon)
        val userLatitudeSecondResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, userLatitudeSecondResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        userLatitudeDocumentContext = JsonPath.parse(userLatitudeSecondResponse.body)
        userLatitudeResponseMessage = userLatitudeDocumentContext.read("$.user_lat")
        assertEquals("User Latitude must be <= 90.0", userLatitudeResponseMessage)
    }

    @Test
    fun `should return error for invalid user longitude parameter`() {
        //Check for invalid data when the user_lon parameter exceeds the negative range.
        var url: String = buildUrl(venueSlug, cartValue, userLat, -185.0)
        val userLongitudeResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, userLongitudeResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        var userLongitudeDocumentContext = JsonPath.parse(userLongitudeResponse.body)
        var userLongitudeResponseMessage = userLongitudeDocumentContext.read<String>("$.user_lon")
        assertEquals("User Longitude must be >= -180.0", userLongitudeResponseMessage)

        //Check for invalid data when the user_lon parameter exceeds the positive range
        url = buildUrl(venueSlug, cartValue, userLat, 185.0)
        val userLongitudeSecondResponse = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, userLongitudeSecondResponse.statusCode, "Expected HTTP 400 BAD REQUEST")
        userLongitudeDocumentContext = JsonPath.parse(userLongitudeSecondResponse.body)
        userLongitudeResponseMessage = userLongitudeDocumentContext.read("$.user_lon")
        assertEquals("User Longitude must be <= 180.0", userLongitudeResponseMessage)
    }

    @Test
    fun `should return error for a user out of distance range`() {
        setUpUserInRangeMoreThan2000m()
        val url: String = buildUrl(venueSlug, cartValue, userLat, userLon)

        val response = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode, "Expected HTTP 400 BAD REQUEST")
        val documentContext = JsonPath.parse(response.body)
        val message: String = documentContext.read("$.message")
        val expectedMessage = "Sorry we can't deliver, you're outside of our delivery range"
        assertEquals(expectedMessage, message)
    }

    @Test
    fun `should add small surcharge to the total fee`() {
        val url: String = buildUrl(venueSlug, 800, userLat, userLon)
        val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode, "Expected HTTP 200 OK")
        assertNotNull(response.body, "Response should not be null")
        val actualResponse: DopcResponse = objectMapper.readValue(response.body!!)

        val expectedResult = DopcResponse(1190, 200, 800, Delivery(190, 177))
        assertEquals(expectedResult, actualResponse, "Response body should match the expected result")
    }
}