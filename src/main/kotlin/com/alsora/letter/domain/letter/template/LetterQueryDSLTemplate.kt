package com.alsora.letter.domain.letter.template

import com.alsora.letter.component.types.LetterDeliveryStatus
import com.alsora.letter.domain.letter.model.LatestLetterDTO
import com.alsora.letter.domain.letter.model.ReceivedLetterDTO
import com.alsora.letter.jooq.tables.Letter
import com.alsora.letter.jooq.tables.LetterDelivery
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class LetterQueryDSLTemplate
@Autowired constructor(
    private val dslContext: DSLContext
) {

    private object DB {
        val letter: Letter = Letter.LETTER
        val delivery: LetterDelivery = LetterDelivery.LETTER_DELIVERY
    }

    fun fetchLatestReceivedLetter(
        deviceId: String
    ): LatestLetterDTO? {
        return dslContext.select(
            DB.letter.ID.`as`("letterId"),
            DB.delivery.ID.`as`("deliveryId"),
            DB.letter.CONTENT,
            DB.delivery.DELIVERED_AT
        ).from(DB.delivery)
            .join(DB.letter)
            .on(DB.delivery.LETTER_ID.eq(DB.letter.ID))
            .where(DB.delivery.RECIPIENT_DEVICE_ID.eq(deviceId))
            .and(DB.delivery.STATUS.eq(LetterDeliveryStatus.DELIVERED.name))
            .orderBy(DB.delivery.DELIVERED_AT.desc())
            .limit(1)
            .fetchOneInto(LatestLetterDTO::class.java)
    }

    fun fetchReceivedLetters(
        deviceId: String
    ): List<ReceivedLetterDTO> {
        return dslContext.select(
            DB.letter.ID,
            DB.letter.CONTENT,
            DB.delivery.DELIVERED_AT
        ).from(DB.delivery)
            .join(DB.letter)
            .on(DB.delivery.LETTER_ID.eq(DB.letter.ID))
            .where(DB.delivery.RECIPIENT_DEVICE_ID.eq(deviceId))
            .and(DB.delivery.STATUS.eq(LetterDeliveryStatus.DELIVERED.name))
            .orderBy(DB.delivery.DELIVERED_AT.desc())
            .fetchInto(ReceivedLetterDTO::class.java)
    }
}


