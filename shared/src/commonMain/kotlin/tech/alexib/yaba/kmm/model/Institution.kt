package tech.alexib.yaba.kmm.model

import kotlin.jvm.JvmInline

//@JvmInline
//value class PlaidInstitutionId(val value: String)

data class Institution(
    val institutionId: String,
    val logo: String,
    val name: String,
    val primaryColor: String,
)