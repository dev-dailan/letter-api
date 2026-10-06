package com.alsora.letter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LetterApiApplication

fun main(args: Array<String>) {
    runApplication<LetterApiApplication>(*args)
}
