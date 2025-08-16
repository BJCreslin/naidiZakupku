package ru.bjcreslin.naidizakupku.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService
import ru.bjcreslin.naidizakupku.common.error.ErrorCode
import ru.bjcreslin.naidizakupku.common.error.ErrorCodeException
import ru.bjcreslin.naidizakupku.common.error.ErrorFactory
import ru.bjcreslin.naidizakupku.common.error.dto.ErrorResponse
import ru.bjcreslin.naidizakupku.common.error.dto.ValidationErrorResponse
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidTokenException
import ru.bjcreslin.naidizakupku.security.exceptions.ResourceNotFoundException
import ru.bjcreslin.naidizakupku.security.exceptions.UnauthorizedException
import ru.bjcreslin.naidizakupku.telegram.exception.TelegramBotServiceException
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler(
    private val errorFactory: ErrorFactory,
    private val customMetricsService: CustomMetricsService
) {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.RESOURCE_NOT_FOUND,
            details = ex.message,
            httpStatus = HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.UNAUTHORIZED,
            details = ex.message,
            httpStatus = HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException, request: WebRequest): ResponseEntity<ErrorResponse> {
        customMetricsService.incrementApiRequestCounter("api.error.invalid_token", 401)
        
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = 401,
            error = "Unauthorized",
            message = ex.message ?: "Invalid token",
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(TelegramBotServiceException::class)
    fun handleTelegramBotServiceException(ex: TelegramBotServiceException): ResponseEntity<ErrorResponse> {
        val errorCode = when {
            ex.message?.contains("must have Telegram Id", ignoreCase = true) == true -> ErrorCode.TELEGRAM_USER_REQUIRED
            else -> ErrorCode.TELEGRAM_SEND_ERROR
        }
        
        return errorFactory.createErrorResponse(
            errorCode = errorCode,
            details = ex.message,
            httpStatus = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(ErrorCodeException::class)
    fun handleErrorCodeException(ex: ErrorCodeException): ResponseEntity<ErrorResponse> {
        val httpStatus = when (ex.errorCode) {
            ErrorCode.RESOURCE_NOT_FOUND, ErrorCode.USER_NOT_FOUND, ErrorCode.PROCUREMENT_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            ErrorCode.FORBIDDEN, ErrorCode.INVALID_TOKEN, ErrorCode.TOKEN_EXPIRED -> HttpStatus.FORBIDDEN
            ErrorCode.VALIDATION_ERROR, ErrorCode.INVALID_INPUT, ErrorCode.REQUIRED_FIELD_MISSING -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        
        return errorFactory.createErrorResponse(
            errorCode = ex.errorCode,
            details = ex.message,
            httpStatus = httpStatus
        )
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: org.springframework.web.bind.MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ValidationErrorResponse> {
        customMetricsService.incrementApiRequestCounter("api.error.validation", 400)
        
        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }
        
        val validationErrorResponse = ValidationErrorResponse(
            timestamp = LocalDateTime.now(),
            status = 400,
            error = "Validation Failed",
            message = "Validation failed for request",
            path = request.getDescription(false),
            fieldErrors = fieldErrors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        customMetricsService.incrementApiRequestCounter("api.error.bad_request", 400)
        
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = 400,
            error = "Bad Request",
            message = ex.message ?: "Invalid request",
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        customMetricsService.incrementApiRequestCounter("api.error.internal", 500)
        
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = 500,
            error = "Internal Server Error",
            message = ex.message ?: "An unexpected error occurred",
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}