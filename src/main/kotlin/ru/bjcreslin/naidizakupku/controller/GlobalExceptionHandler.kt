package ru.bjcreslin.naidizakupku.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import ru.bjcreslin.naidizakupku.common.error.ErrorCode
import ru.bjcreslin.naidizakupku.common.error.ErrorCodeException
import ru.bjcreslin.naidizakupku.common.error.ErrorFactory
import ru.bjcreslin.naidizakupku.common.error.dto.ErrorResponse
import ru.bjcreslin.naidizakupku.common.error.dto.ValidationErrorResponse
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidTokenException
import ru.bjcreslin.naidizakupku.security.exceptions.ResourceNotFoundException
import ru.bjcreslin.naidizakupku.security.exceptions.UnauthorizedException
import ru.bjcreslin.naidizakupku.telegram.exception.TelegramBotServiceException

@ControllerAdvice
class GlobalExceptionHandler(
    private val errorFactory: ErrorFactory
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
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ErrorResponse> {
        val errorCode = when {
            ex.message?.contains("expired", ignoreCase = true) == true -> ErrorCode.TOKEN_EXPIRED
            else -> ErrorCode.INVALID_TOKEN
        }
        
        return errorFactory.createErrorResponse(
            errorCode = errorCode,
            details = ex.message,
            httpStatus = HttpStatus.FORBIDDEN
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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors
            .associate { it.field to (it.defaultMessage ?: "Некорректное значение") }
        
        return errorFactory.createValidationErrorResponse(fieldErrors)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.INVALID_INPUT,
            details = ex.message,
            httpStatus = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<ErrorResponse> {
        return errorFactory.createErrorResponse(
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
            details = ex.message,
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}