package com.alsora.letter.domain.letter.repository

import com.alsora.letter.domain.letter.entity.LetterDelivery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LetterDeliveryRepository: JpaRepository<LetterDelivery, Long> {

    fun existsByRecipientDeviceIdAndReadAtIsNull(recipientDeviceId: String): Boolean

    /**
     * 배달 시각이 지난 PENDING 건을 잠그고 가져온다.
     * 다른 배치가 이미 잡고 있는 행은 건너뛰어(SKIP LOCKED) 배치가 동시에 돌아도 중복 처리되지 않는다.
     */
    @Query(
        value = """
            SELECT * FROM letter_delivery
            WHERE status = 'PENDING' AND scheduled_at <= now()
            ORDER BY scheduled_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
        """,
        nativeQuery = true
    )
    fun findDueForUpdate(limit: Int): List<LetterDelivery>
}
