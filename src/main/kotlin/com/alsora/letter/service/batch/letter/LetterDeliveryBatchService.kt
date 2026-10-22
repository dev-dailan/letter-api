package com.alsora.letter.service.batch.letter

import com.alsora.letter.component.types.LetterDeliveryStatus
import com.alsora.letter.domain.letter.repository.LetterDeliveryRepository
import com.alsora.letter.domain.letter.repository.LetterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class LetterDeliveryBatchService
@Autowired constructor(
    private val letterRepository: LetterRepository,
    private val letterDeliveryRepository: LetterDeliveryRepository
) {

    /**
     * scheduled_at 이 지난 PENDING 건에 랜덤 편지를 배정하고 DELIVERED 로 바꾼다.
     * 배정할 편지가 없으면 PENDING 으로 두고 다음 배치에서 다시 시도한다.
     *
     * @return 배달 완료한 건수
     */
    @Transactional
    fun deliverDueLetters(limit: Int = DEFAULT_BATCH_SIZE): Int {
        val dueDeliveries = letterDeliveryRepository.findDueForUpdate(limit)
        var deliveredCount = 0

        for (delivery in dueDeliveries) {
            val letterId = letterRepository.findRandomDeliverableLetterId(delivery.recipientDeviceId)
            if (letterId == null) {
                log.debug("배정할 편지가 없어 다음 배치로 미룹니다. deliveryId={}", delivery.id)
                continue
            }

            delivery.letterId = letterId
            delivery.status = LetterDeliveryStatus.DELIVERED
            delivery.deliveredAt = ZonedDateTime.now()
            // 같은 수신자의 다음 건이 방금 배정한 편지를 다시 고르지 않도록 바로 반영한다.
            letterDeliveryRepository.flush()
            deliveredCount++
        }

        if (dueDeliveries.isNotEmpty()) {
            log.info("편지 배달 배치 완료. 대상={}, 배달={}", dueDeliveries.size, deliveredCount)
        }
        return deliveredCount
    }

    companion object {
        private val log = LoggerFactory.getLogger(LetterDeliveryBatchService::class.java)
        const val DEFAULT_BATCH_SIZE = 500
    }
}
