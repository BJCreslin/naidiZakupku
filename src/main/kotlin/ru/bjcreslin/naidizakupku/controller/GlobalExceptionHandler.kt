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
        
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.INVALID_TOKEN,
            details = ex.message,
            httpStatus = HttpStatus.UNAUTHORIZED
        )
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
        
        val fieldErrors = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
        }
        
        return errorFactory.createValidationErrorResponse(
            fieldErrors = fieldErrors,
            customMessage = "Validation failed for request"
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        customMetricsService.incrementApiRequestCounter("api.error.bad_request", 400)
        
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.INVALID_INPUT,
            details = ex.message,
            httpStatus = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        customMetricsService.incrementApiRequestCounter("api.error.internal", 500)
        
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
            details = ex.message,
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}