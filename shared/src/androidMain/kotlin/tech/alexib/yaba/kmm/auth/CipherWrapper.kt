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

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class CipherWrapper {

    companion object {
        const val TRANSFORMATION_SYMMETRIC = "AES/CBC/PKCS7Padding"
        const val IV_SEPARATOR = "]"
    }

    val cipher: Cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC)

    fun encrypt(data: String, key: Key): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
        var result = ivString + IV_SEPARATOR

        val bytes = cipher.doFinal(data.toByteArray())
        result += Base64.encodeToString(bytes, Base64.DEFAULT)

        return result
    }

    fun decrypt(data: String, key: Key): String {
        val split = data.split(IV_SEPARATOR.toRegex())

        require(split.size == 2)

        val ivString = split[0]
        val encodedString = split[1]
        val ivSpec = IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val encryptedData = Base64.decode(encodedString, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }
}
