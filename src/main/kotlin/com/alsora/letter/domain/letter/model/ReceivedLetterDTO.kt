package com.alsora.letter.domain.letter.model

import java.time.OffsetDateTime

data class ReceivedLetterDTO (
    val id: Long,
    val content: String,
    val deliveredAt: OffsetDateTime
)