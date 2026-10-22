package com.alsora.letter.component.exception

import org.springframework.http.HttpStatus

/** API 공통 에러 응답 형식 */
data class ErrorResponse(
    val code: String,
    val message: String
) {
    companion object {
        fun of(status: HttpStatus, message: String) = ErrorResponse(status.name, message)
    }
}
