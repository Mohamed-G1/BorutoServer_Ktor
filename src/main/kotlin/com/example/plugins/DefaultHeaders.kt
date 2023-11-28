package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
import java.time.Duration

/**
 * this class to send a default header with the request
 * So here we'll send this custom header with each and every response we send to our client
 * */
fun Application.configureDefaultHeader() {
    install(DefaultHeaders) {

        // here we specify how long we want to cache that data
        val oneYearInSeconds = Duration.ofDays(365).seconds
        header(
            name = HttpHeaders.CacheControl,
            value = "public max-age=$oneYearInSeconds , immutable"
        )
    }
}