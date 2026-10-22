package com.alsora.letter.component.lambda

import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

/**
 * Lambda SnapStart 스냅샷 직전에 Lambda Web Adapter 가 호출한다.
 * 스냅샷에 담긴 DB 커넥션은 복원 후 쓸 수 없으므로 미리 비워 둔다.
 * API Gateway 를 통한 외부 요청은 Web Adapter 가 403 으로 막는다.
 */
@Profile("prod")
@RestController
class SnapStartHooks(private val dataSource: DataSource) {

    @PostMapping("/internal/snapstart/before-checkpoint")
    fun beforeCheckpoint(): ResponseEntity<Void> {
        dataSource.unwrap(HikariDataSource::class.java).hikariPoolMXBean?.softEvictConnections()
        return ResponseEntity.noContent().build()
    }
}
