package wolt.dopc.Exeption

import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import wolt.dopc.dto.InvalidDistanceResponse

/**
 * This class is used to handle the global exceptions.
**/
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(err: MethodArgumentNotValidException)
            : ResponseEntity<Map<String, String>> {
        val errors = err.bindingResult.fieldErrors.associate { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
        fieldName to errorMessage
        }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleHttpClientErrorException(err: HttpClientErrorException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(err.responseBodyAsString)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(err: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Invalid input data: ${err.message}")
    }

    @ExceptionHandler(InvalidDistanceException::class)
    fun handleInvalidDistanceException(err: InvalidDistanceException): ResponseEntity<InvalidDistanceResponse>{
        val response = InvalidDistanceResponse(message = err.message)
        return ResponseEntity.badRequest().body(response)
    }
}