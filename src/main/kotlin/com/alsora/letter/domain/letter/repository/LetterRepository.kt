package com.alsora.letter.domain.letter.repository

import com.alsora.letter.component.types.SendType
import com.alsora.letter.domain.letter.entity.Letter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LetterRepository: JpaRepository<Letter, Long> {
    fun existsBySenderDeviceId(senderDeviceId: String): Boolean

    fun findFirstBySenderDeviceIdAndSendTypeOrderByCreatedAtDesc(
        senderDeviceId: String,
        sendType: SendType
    ): Letter?

    /** 수신자가 보내지 않았고, 아직 받은 적 없는 편지 중 하나를 랜덤으로 고른다. */
    @Query(
        value = """
            SELECT l.id FROM letter l
            WHERE l.sender_device_id <> :recipientDeviceId
              AND NOT EXISTS (
                  SELECT 1 FROM letter_delivery d
                  WHERE d.recipient_device_id = :recipientDeviceId AND d.letter_id = l.id
              )
            ORDER BY random()
            LIMIT 1
        """,
        nativeQuery = true
    )
    fun findRandomDeliverableLetterId(recipientDeviceId: String): Long?
}
