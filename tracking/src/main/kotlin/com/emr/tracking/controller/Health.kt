package com.emr.tracking.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Health {
    @GetMapping("/healthCheck")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("System is online")
    }
}