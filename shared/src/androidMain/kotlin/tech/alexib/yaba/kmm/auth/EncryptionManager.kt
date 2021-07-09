/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.kmm.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt
import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private val log = Kermit()

internal object EncryptionManager {

    private const val KEY_NAME = "yaba_kmm_key"
    private const val PROVIDER = "AndroidKeyStore"

    @Synchronized
    fun encrypt(data: String): String {
        return CipherWrapper().encrypt(data, getKey())
    }

    @Synchronized
    fun decrypt(data: String): String {
        return CipherWrapper().decrypt(data, getKey())
    }

    fun getCryptoObject(): BiometricPrompt.CryptoObject {
        val cipher = CipherWrapper().cipher
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        return BiometricPrompt.CryptoObject(cipher)
    }

    private val keyStore: KeyStore = KeyStore.getInstance(PROVIDER)

    init {
        ensureNeverFrozen()
        keyStore.load(null)
    }

    private fun getKey() = getSecretKey()

    private fun getSecretKey(): SecretKey {
        try {
            keyStore
                .getKey(KEY_NAME, null)
                ?.let {
                    return it as SecretKey
                }
        } catch (e: UnrecoverableKeyException) {
            log.e { e.localizedMessage ?: "Could not get key" }
            throw e
        }
        return createKey()
    }

    @Synchronized
    private fun createKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            PROVIDER
        )

        keyGenerator.init(
            KeyGenParameterSpec
                .Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(false)
                .setInvalidatedByBiometricEnrollment(true)
                .setRandomizedEncryptionRequired(false)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )

        return keyGenerator.generateKey()
    }
}
