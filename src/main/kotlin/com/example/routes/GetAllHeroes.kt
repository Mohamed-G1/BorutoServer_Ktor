package com.example.routes

import com.example.models.ApiResponse
import com.example.repository.HeroRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getAllHeroes() {
    val heroesRepository: HeroRepository by inject()

    get("/boruto/heroes") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            /**
             *  this function will pass Throws an IllegalArgumentException if the value is false.*/
            require(page in 1..5)

            val heroesResponse = heroesRepository.getAllHeroes(page = page)

            call.respond(
                message = heroesResponse,
                status = HttpStatusCode.OK
            )
        } catch (n: NumberFormatException) {
            call.respond(
                message = ApiResponse(
                    success = false,
                    message = "Only Number Allowed"
                ),
                status = HttpStatusCode.BadRequest
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                message = ApiResponse(
                    success = false,
                    message = "Heroes Not Found."
                ),
                status = HttpStatusCode.NotFound
            )
        }
    }
}