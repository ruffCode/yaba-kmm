package tech.alexib.yaba.model

import kotlin.jvm.JvmInline

@JvmInline
value class PlaidInstitutionId(val value: String)

data class Institution(
    val institutionId: PlaidInstitutionId,
    val logo: String,
    val name: String,
    val primaryColor: String,
)