package tech.alexib.yaba.kmm.model

import com.benasher44.uuid.Uuid

data class User(
    val id: Uuid,
    val email: String
)
