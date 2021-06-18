package tech.alexib.yaba.model.response

import com.benasher44.uuid.Uuid

data class AuthResponse(
    val id: Uuid,
    val email: String,
    val token: String
)
