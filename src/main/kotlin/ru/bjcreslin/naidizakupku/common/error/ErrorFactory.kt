package ru.bjcreslin.naidizakupku.common.error

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import ru.bjcreslin.naidizakupku.common.error.dto.ErrorResponse
import ru.bjcreslin.naidizakupku.common.error.dto.ValidationErrorResponse
import java.util.*

@Component
class ErrorFactory {
    
    private val logger = LoggerFactory.getLogger(ErrorFactory::class.java)
    
    /**
     * Создает стандартизованный ответ об ошибке
     */
    fun createErrorResponse(
        errorCode: ErrorCode,
        customMessage: String? = null,
        details: String? = null,
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    ): ResponseEntity<ErrorResponse> {
        val traceId = generateTraceId()
        val path = getCurrentRequestPath()
        
        val errorResponse = ErrorResponse(
            code = errorCode.code,
            message = customMessage ?: errorCode.defaultMessage,
            details = details,
            path = path,
            traceId = traceId
        )
        
        logError(errorCode, errorResponse, httpStatus)
        
        return ResponseEntity(errorResponse, httpStatus)
    }
    
    /**
     * Создает ответ об ошибке валидации
     */
    fun createValidationErrorResponse(
        fieldErrors: Map<String, String>,
        customMessage: String? = null
    ): ResponseEntity<ValidationErrorResponse> {
        val traceId = generateTraceId()
        val path = getCurrentRequestPath()
        
        val errorResponse = ValidationErrorResponse(
            code = ErrorCode.VALIDATION_ERROR.code,
            message = customMessage ?: ErrorCode.VALIDATION_ERROR.defaultMessage,
            fieldErrors = fieldErrors,
            path = path,
            traceId = traceId
        )
        
        logger.warn("Validation error occurred. TraceId: $traceId, Path: $path, Errors: $fieldErrors")
        
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
    
    /**
     * Создает ответ об ошибке на основе исключения
     */
    fun createErrorFromException(
        exception: Exception,
        errorCode: ErrorCode = ErrorCode.INTERNAL_SERVER_ERROR,
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    ): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            errorCode = errorCode,
            details = exception.message,
            httpStatus = httpStatus
        )
    }
    
    /**
     * Вспомогательные методы для создания специфических ошибок
     */
    fun createNotFoundError(resourceName: String, resourceId: Any? = null): ResponseEntity<ErrorResponse> {
        val details = if (resourceId != null) {
            "$resourceName с ID '$resourceId' не найден"
        } else {
            "$resourceName не найден"
        }
        
        return createErrorResponse(
            errorCode = ErrorCode.RESOURCE_NOT_FOUND,
            details = details,
            httpStatus = HttpStatus.NOT_FOUND
        )
    }
    
    fun createUnauthorizedError(details: String? = null): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            errorCode = ErrorCode.UNAUTHORIZED,
            details = details,
            httpStatus = HttpStatus.UNAUTHORIZED
        )
    }
    
    fun createForbiddenError(details: String? = null): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            errorCode = ErrorCode.FORBIDDEN,
            details = details,
            httpStatus = HttpStatus.FORBIDDEN
        )
    }
    
    fun createBadRequestError(details: String? = null): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            errorCode = ErrorCode.INVALID_INPUT,
            details = details,
            httpStatus = HttpStatus.BAD_REQUEST
        )
    }
    
    /**
     * Создает RuntimeException с кодом ошибки для использования в сервисах
     */
    fun createException(errorCode: ErrorCode, details: String? = null): RuntimeException {
        val message = details?.let { "${errorCode.defaultMessage}: $it" } ?: errorCode.defaultMessage
        return ErrorCodeException(errorCode, message)
    }
    
    private fun generateTraceId(): String {
        return MDC.get("traceId") ?: UUID.randomUUID().toString().replace("-", "").substring(0, 8)
    }
    
    private fun getCurrentRequestPath(): String? {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            requestAttributes?.request?.requestURI
        } catch (e: Exception) {
            null
        }
    }
    
    private fun logError(errorCode: ErrorCode, errorResponse: ErrorResponse, httpStatus: HttpStatus) {
        when {
            httpStatus.is5xxServerError -> {
                logger.error("Server error occurred. Code: ${errorCode.code}, Message: ${errorResponse.message}, TraceId: ${errorResponse.traceId}")
            }
            httpStatus.is4xxClientError -> {
                logger.warn("Client error occurred. Code: ${errorCode.code}, Message: ${errorResponse.message}, TraceId: ${errorResponse.traceId}")
            }
            else -> {
                logger.info("Error response created. Code: ${errorCode.code}, Message: ${errorResponse.message}, TraceId: ${errorResponse.traceId}")
            }
        }
    }
}

/**
 * Специальное исключение с кодом ошибки
 */
class ErrorCodeException(
    val errorCode: ErrorCode,
    message: String
) : RuntimeException(message)
