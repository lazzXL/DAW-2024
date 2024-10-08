package pt.isel.http_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HttpApiApplication

fun main(args: Array<String>) {
	runApplication<HttpApiApplication>(*args)
}
