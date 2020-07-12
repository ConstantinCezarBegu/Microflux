package com.example.constaflux2.database.util

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.nio.charset.StandardCharsets


class AesEncryption(context: Context) {
    companion object {
        private const val PREF_FILE_NAME = "microflux_pref"
        private const val TINK_KEY_SET_NAME = "microflux_keyset"
        private const val MASTER_KEY_URI = "android-keystore://microflux_master_key"
        private const val ASSOCIATED_DATA = "microflux_associated_data"
    }

    init {
        TinkConfig.register()
    }

    private val aead = AndroidKeysetManager.Builder()
        .withSharedPref(context, TINK_KEY_SET_NAME, PREF_FILE_NAME)
        .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
        .withMasterKeyUri(MASTER_KEY_URI)
        .build()
        .keysetHandle
        .getPrimitive(Aead::class.java)

    fun encryptData(data: String): ByteArray = aead.encrypt(
        data.toByteArray(StandardCharsets.UTF_8),
        ASSOCIATED_DATA.toByteArray(StandardCharsets.UTF_8)
    )

    fun decryptData(data: ByteArray): String = String(
        aead.decrypt(data, ASSOCIATED_DATA.toByteArray(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8
    )
}