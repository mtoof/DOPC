package wolt.dopc.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * This class is used to configure the RestTemplate bean.
**/
@Configuration
class DopcConfig {

    @Bean
    fun restTemplate(): RestTemplate{
        return RestTemplate()
    }
}