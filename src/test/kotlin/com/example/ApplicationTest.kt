package com.example

import com.example.di.koinModule
import com.example.models.ApiResponse
import com.example.models.Hero
import com.example.plugins.*
import com.example.repository.HeroRepository
import com.example.repository.HeroRepositoryImpl
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }


    @Test
    fun testRoot2() {
        testApplication {
            application { configureRouting() }
            client.get("/").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = status)
                assertEquals(expected = "Hello", actual = bodyAsText())
            }
        }

    }

    @Test
    fun `access all heroes, assert correct information`() {
        testApplication {
            environment { developmentMode = false

            }

            application { configureRouting() }
            client.get("/boruto/heroes").apply {

                val expected = ApiResponse(
                    success = true,
                    message = "Success",
                    prevPage = null,
                    nextPage = 2,
                    heroes = heroRepository.page1

                )

                /**
                 * this to decode and convert the JSON string into the ApiResponse object*/
                val actual = Json.decodeFromString<ApiResponse>(bodyAsText())

                assertEquals(expected = HttpStatusCode.OK, actual = status)
                assertEquals(expected = expected, actual = actual)
            }
        }
    }


    @Test
    fun `access all heroes, all pages, assert correct information`() {
        val heroRepository: HeroRepository by inject(HeroRepository::class.java)
        testApplication {
            application {
                configureKoin()
                configureRouting()
            }
            val page = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5,
            )

            page.forEach { pages ->
                client.get("/boruto/heroes?page=$pages").apply {
                    val expected = ApiResponse(
                        success = true,
                        message = "Success",
                        prevPage = calculatePages(pages)["prevPage"] ,
                        nextPage = calculatePages(pages)["nextPage"],
                        heroes = heroes[pages - 1]
                    )

                    val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                    assertEquals(expected = HttpStatusCode.OK, actual = status)
                    assertEquals(expected = expected, actual = actual)
                }

            }

        }
    }

    private fun calculatePages(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4)
            nextPage = page.plus(1)
        if (page in 2..5)
            prevPage = page.minus(1)
        if (page == 1)
            prevPage = null
        if (page == 5)
            nextPage = null
        return mapOf("prevPage" to prevPage, "nextPage" to nextPage)
    }
}
