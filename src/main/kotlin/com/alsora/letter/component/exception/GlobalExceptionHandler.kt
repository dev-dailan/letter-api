package com.alsora.letter.component.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ResponseEntity<ErrorResponse> =
        response(HttpStatus.NOT_FOUND, e.message ?: "데이터가 없습니다.")

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> =
        response(HttpStatus.BAD_REQUEST, "필수 파라미터가 없습니다. (${e.parameterName})")

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> =
        response(HttpStatus.BAD_REQUEST, "파라미터 형식이 올바르지 않습니다. (${e.name})")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        response(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다.")

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(e: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> =
        response(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다. (${e.method})")

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(e: NoResourceFoundException): ResponseEntity<ErrorResponse> =
        response(HttpStatus.NOT_FOUND, "존재하지 않는 경로입니다.")

    /** 위에서 처리하지 못한 예외. 내부 정보가 새지 않도록 메시지는 고정하고 로그만 남긴다. */
    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("처리되지 않은 예외", e)
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.")
    }

    private fun response(status: HttpStatus, message: String): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(ErrorResponse.of(status, message))

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}
