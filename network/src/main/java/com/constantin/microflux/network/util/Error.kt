package com.constantin.microflux.network.util

import com.constantin.microflux.data.Result
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.RedirectResponseException
import io.ktor.client.features.ResponseException
import io.ktor.client.features.ServerResponseException
import io.ktor.utils.io.errors.IOException

inline fun <reified T : Any> error(
    block: () -> T
)= try {
    val blockVar = block()
    Result.success(blockVar)
} catch (e: ClientRequestException) {
    when (e.response.status.value) {
        401 -> Result.Error.NetworkError.AuthorizationError
        404 -> Result.Error.NetworkError.ServerUrlError
        else -> Result.Error.NetworkError.ConnectivityError
    }
} catch (e: RedirectResponseException) {
    Result.Error.NetworkError.RedirectResponseError
} catch (e: ServerResponseException) {
    Result.Error.NetworkError.ServerResponseError
} catch (e: ResponseException) {
    Result.Error.NetworkError.ResponseError
} catch (e: IOException) {
    Result.Error.NetworkError.IOError
}