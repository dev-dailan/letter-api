package com.alsora.letter.service.rest.letter.controller

import com.alsora.letter.domain.letter.model.ReceivedLetterDTO
import com.alsora.letter.service.rest.letter.dto.LetterParam
import com.alsora.letter.service.rest.letter.dto.LetterResult
import com.alsora.letter.service.rest.letter.service.LetterCommandService
import com.alsora.letter.service.rest.letter.service.LetterQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("")
class LetterController
@Autowired constructor(
    private val letterQueryService: LetterQueryService,
    private val letterCommandService: LetterCommandService
){
    @GetMapping("/status")
    fun fetchLetterStatus(
        @RequestParam(value = "device_id", required = true) deviceId: String
    ): ResponseEntity<LetterResult.Status> {
        val result = letterQueryService.fetchLetterStatus(deviceId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/send")
    fun sendLetter(
        @RequestBody payload: LetterParam.Send
    ): ResponseEntity<Boolean> {
        letterCommandService.sendLetter(payload)
        return ResponseEntity.created(URI("")).body(true)
    }

    @GetMapping("/received/latest")
    fun fetchLatestReceivedLetter(
        @RequestParam(value = "device_id", required = true) deviceId: String
    ): ResponseEntity<Any> {
        val result = letterQueryService.fetchLatestReceivedLetter(deviceId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/received")
    fun fetchReceivedLetters(
        @RequestParam(value = "device_id", required = true) deviceId: String
    ): ResponseEntity<List<ReceivedLetterDTO>> {
        val result = letterQueryService.fetchReceivedLetters(deviceId)
        return ResponseEntity.ok(result)
    }
}