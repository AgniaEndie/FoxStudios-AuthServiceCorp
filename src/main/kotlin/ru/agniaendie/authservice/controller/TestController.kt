package ru.agniaendie.authservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("main")
class TestController {
    @GetMapping("test")
    suspend fun test() : String{
        println("test")
        return "test"
    }
}
