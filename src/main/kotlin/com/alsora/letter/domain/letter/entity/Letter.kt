package com.alsora.letter.domain.letter.entity

import com.alsora.letter.component.types.SendType
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
@Table(name = "letter")
class Letter{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "sender_device_id", nullable = false, updatable = false, length = 100)
    var senderDeviceId: String = ""

    @Column(name = "content", nullable = false, updatable = false, columnDefinition = "text")
    var content: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "send_type", nullable = false, updatable = false, length = 10)
    var sendType: SendType = SendType.FREE

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now()
}
