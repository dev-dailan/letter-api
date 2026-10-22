package com.alsora.letter.service.batch.letter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = ["letter.delivery.scheduler.enabled"], havingValue = "true", matchIfMissing = true)
class LetterDeliveryScheduler
@Autowired constructor(
    private val letterDeliveryBatchService: LetterDeliveryBatchService
) {

    @Scheduled(fixedDelayString = "\${letter.delivery.scheduler.fixed-delay:PT1M}", initialDelayString = "PT10S")
    fun deliverDueLetters() {
        letterDeliveryBatchService.deliverDueLetters()
    }
}
