package com.alsora.letter.service.rest.letter.service

import com.alsora.letter.component.exception.NotFoundException
import com.alsora.letter.component.types.SendType
import com.alsora.letter.domain.letter.model.LatestLetterDTO
import com.alsora.letter.domain.letter.model.ReceivedLetterDTO
import com.alsora.letter.domain.letter.repository.LetterDeliveryRepository
import com.alsora.letter.domain.letter.repository.LetterRepository
import com.alsora.letter.domain.letter.template.LetterQueryDSLTemplate
import com.alsora.letter.service.rest.letter.dto.LetterResult
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZonedDateTime

@Service
class LetterQueryService
@Autowired constructor(
    private val letterRepository: LetterRepository,
    private val letterDeliveryRepository: LetterDeliveryRepository,
    private val letterQueryDSLTemplate: LetterQueryDSLTemplate
){
    companion object {
        private val FREE_TICKET_COOLDOWN: Duration = Duration.ofHours(24)
    }

    fun fetchLetterStatus(
        deviceId: String
    ): LetterResult.Status{
        val hasSentLetter = letterRepository.existsBySenderDeviceId(deviceId)

        // 보낸 적이 없으면 FREE 를 쓴 적도 없으므로 조회를 생략한다.
        val lastFreeLetter = if (hasSentLetter) {
            letterRepository.findFirstBySenderDeviceIdAndSendTypeOrderByCreatedAtDesc(deviceId, SendType.FREE)
        } else {
            null
        }

        // 마지막 FREE 발송 후 24시간이 지나야 다시 FREE 로 보낼 수 있다.
        val hasFreeTicket = lastFreeLetter == null ||
                !lastFreeLetter.createdAt.isAfter(ZonedDateTime.now().minus(FREE_TICKET_COOLDOWN))


        val hasUnreadLetter = if (hasSentLetter) {
            false
        } else {
            letterDeliveryRepository.existsByRecipientDeviceIdAndReadAtIsNull(deviceId)
        }

        return LetterResult.Status.of(hasSentLetter, hasFreeTicket, hasUnreadLetter)

    }

    @Transactional
    fun fetchLatestReceivedLetter(
        deviceId: String
    ): LatestLetterDTO {
        val latestLetter = letterQueryDSLTemplate.fetchLatestReceivedLetter(deviceId)?: throw NotFoundException()

        val delivery = letterDeliveryRepository.findById(latestLetter.deliveryId).orElseThrow { NotFoundException() }
        delivery.readAt = ZonedDateTime.now()

        return latestLetter
    }

    fun fetchReceivedLetters(
        deviceId: String
    ): List<ReceivedLetterDTO> {
        return letterQueryDSLTemplate.fetchReceivedLetters(deviceId)
    }
}
