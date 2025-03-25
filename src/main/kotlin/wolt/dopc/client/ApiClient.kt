package wolt.dopc.client

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import wolt.dopc.dto.DeliverySpecs
import wolt.dopc.dto.DynamicApiResponse
import wolt.dopc.dto.StaticApiResponse


@Component
class ApiClient(val restTemplate: RestTemplate) {

    /**
        * This function will fetch the data from the static API and return the coordinates of the venue.
        * @param venueSlug: The slug of the venue whose coordinates are to be fetched.
        * @return List<Double>: The coordinates of the venue.
    **/
    
    fun fetchDataFromStaticAPI(venueSlug: String): List<Double>{
        val url: String = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/${venueSlug}/static"
        val response: StaticApiResponse = restTemplate.getForObject(url, StaticApiResponse::class.java)
            ?: throw HttpClientErrorException(HttpStatus.BAD_REQUEST)
        return try {
            response.venueRaw.location.coordinates
        }
        catch (err: Exception){
            throw IllegalArgumentException(err.message)
        }
    }

    /**
        * This function will fetch the data from the dynamic API and return the delivery specs of the venue.
        * @param venueSlug: The slug of the venue whose delivery specs are to be fetched.
        * @return DeliverySpecs: The delivery specs of the venue.
    **/

    fun fetchDataFromDynamicAPI(venueSlug: String): DeliverySpecs {
        val url: String = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/${venueSlug}/dynamic"
        val response = restTemplate.getForObject(url, DynamicApiResponse::class.java)
            ?: throw HttpClientErrorException(HttpStatus.BAD_REQUEST)
        return try {
            response.venueRaw.deliverySpecs
        }
        catch (err: Exception){
            throw IllegalArgumentException(err.message)
        }
    }

}