package com.alsora.letter.component.exception

/** 조회 결과가 없을 때 던진다. [GlobalExceptionHandler] 가 404 Not Found 로 응답한다. */
class NotFoundException(
    message: String = "데이터가 없습니다."
) : RuntimeException(message)
