package com.alsora.letter.domain.letter.entity

import com.alsora.letter.component.types.LetterDeliveryStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "letter_delivery")
class LetterDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "recipient_device_id", nullable = false, updatable = false, length = 100)
    var recipientDeviceId: String = ""

    @Column(name = "source_letter_id", nullable = false, updatable = false)
    var sourceLetterId: Long? = null

    @Column(name = "letter_id", nullable = true, updatable = true)
    var letterId: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    var status: LetterDeliveryStatus = LetterDeliveryStatus.PENDING

    @Column(name = "scheduled_at", nullable = false, updatable = false)
    var scheduledAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "delivered_at")
    var deliveredAt: ZonedDateTime? = null

    @Column(name = "read_at")
    var readAt: ZonedDateTime? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now()
}
