package tech.alexib.yaba.kmm.model.response

import com.benasher44.uuid.Uuid


data class PlaidItemCreateResponse(
    val id:Uuid,
    val name:String,
    val logo:String,
    val accounts:List<Account>
){
    data class Account(
        val mask:String,
        val name:String,
        val plaidAccountId:String
    )
}
