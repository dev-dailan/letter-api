package com.alsora.letter.domain.letter.model

import java.time.OffsetDateTime

data class LatestLetterDTO (
    val letterId: Long,
    val deliveryId: Long,
    val content: String,
    val deliveredAt: OffsetDateTime
)