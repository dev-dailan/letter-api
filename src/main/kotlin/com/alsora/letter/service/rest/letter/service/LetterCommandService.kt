package com.alsora.letter.service.rest.letter.service

import com.alsora.letter.component.types.LetterDeliveryStatus
import com.alsora.letter.domain.letter.entity.Letter
import com.alsora.letter.domain.letter.entity.LetterDelivery
import com.alsora.letter.domain.letter.repository.LetterDeliveryRepository
import com.alsora.letter.domain.letter.repository.LetterRepository
import com.alsora.letter.service.rest.letter.dto.LetterParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ThreadLocalRandom

@Service
class LetterCommandService
@Autowired constructor(
    private val letterRepository: LetterRepository,
    private val letterDeliveryRepository: LetterDeliveryRepository
) {

    @Transactional
    fun sendLetter(
        param: LetterParam.Send
    ) {

        val createdLetter = letterRepository.save(
            Letter().apply {
                this.senderDeviceId = param.deviceId
                this.sendType = param.sendType
                this.content = param.content
            }
        )

        val now = ZonedDateTime.now()
        letterDeliveryRepository.save(
            LetterDelivery().apply {
                this.recipientDeviceId = param.deviceId
                this.sourceLetterId = createdLetter.id
                this.status = LetterDeliveryStatus.PENDING
                this.scheduledAt = now.plus(randomDeliveryDelay())
                this.createdAt = now
            }
        )
    }

    /** 배달 지연 시간. [MIN_DELIVERY_DELAY] ~ [MAX_DELIVERY_DELAY] 사이에서 초 단위로 랜덤 */
    private fun randomDeliveryDelay(): Duration =
        Duration.ofSeconds(
            ThreadLocalRandom.current().nextLong(MIN_DELIVERY_DELAY.seconds, MAX_DELIVERY_DELAY.seconds + 1)
        )

    companion object {
        private val MIN_DELIVERY_DELAY: Duration = Duration.ofMinutes(10)
        private val MAX_DELIVERY_DELAY: Duration = Duration.ofHours(5)
    }
}