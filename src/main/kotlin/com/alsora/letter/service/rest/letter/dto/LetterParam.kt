package com.alsora.letter.service.rest.letter.dto

import com.alsora.letter.component.types.SendType

object LetterParam {

    data class Send(
        val deviceId: String,
        val sendType: SendType,
        val content: String
    )
}