package com.constantin.microflux.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@OptIn(UnstableDefault::class)
class MinifluxService(
    engine: HttpClientEngineFactory<HttpClientEngineConfig>
) {
    private val client = HttpClient(engine) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json(JsonConfiguration(ignoreUnknownKeys = true)))
        }
        HttpResponseValidator {
            validateResponse { response ->
                val statusCode = response.status.value

                when (statusCode) {
                    in 300..399 -> throw RedirectResponseException(response)
                    in 400..499 -> throw ClientRequestException(response)
                    in 500..599 -> throw ServerResponseException(response)
                }

                if (statusCode >= 600) {
                    throw ResponseException(response)
                }
            }
        }
    }

    val entry = EntryNetwork(client = client)
    val feed = FeedNetwork(client = client)
    val category = CategoryNetwork(client = client)
    val me = MeNetwork(client = client)
}