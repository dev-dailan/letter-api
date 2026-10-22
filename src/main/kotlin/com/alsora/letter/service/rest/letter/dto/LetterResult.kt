package com.alsora.letter.service.rest.letter.dto

object LetterResult {

    data class Status(
        val hasSentLetter: Boolean,
        val hasFreeTicket: Boolean,
        val hasUnreadLetter: Boolean
    ) {
        companion object {
            fun of(hasSentLetter: Boolean, hasFreeTicket: Boolean, hasUnreadLetter: Boolean) = Status(hasSentLetter, hasFreeTicket, hasUnreadLetter)
        }
    }
}
