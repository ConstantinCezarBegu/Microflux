package com.constantin.microflux.network

import com.constantin.microflux.data.Result
import com.constantin.microflux.network.data.AccountPassword
import com.constantin.microflux.network.data.AccountUrl
import com.constantin.microflux.network.data.AccountUsername
import com.constantin.microflux.network.data.MeResponse
import com.constantin.microflux.network.util.Credentials
import com.constantin.microflux.network.util.get
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