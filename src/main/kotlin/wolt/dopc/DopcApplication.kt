package wolt.dopc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DopcApplication

fun main(args: Array<String>) {
    runApplication<DopcApplication>(*args)
}
