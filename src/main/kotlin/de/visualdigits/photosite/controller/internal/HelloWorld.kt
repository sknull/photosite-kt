package de.visualdigits.photosite.controller.internal

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorld {

    @GetMapping(value = ["/internal/hello"])
    fun helloWorld(): String = "hello world!"
}