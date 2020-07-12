package com.example.constaflux2.network

import com.example.constaflux2.data.Result
import com.example.constaflux2.network.data.AccountPassword
import com.example.constaflux2.network.data.AccountUrl
import com.example.constaflux2.network.data.AccountUsername
import com.example.constaflux2.network.data.MeResponse
import com.example.constaflux2.network.util.Credentials
import com.example.constaflux2.network.util.get
import io.ktor.client.HttpClient

class MeNetwork(
    private val client: HttpClient
) {

    companion object {
        const val ME_URL = "/v1/me"
    }

    suspend fun get(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword
    ): Result<MeResponse> {
        return client.get(
            urlString = "${accountUrl}${ME_URL}",
            auth = Credentials.basic(
                userName = accountUsername,
                password = accountPassword
            )
        )
    }
}