package tech.alexib.yaba.kmm.model.response

data class CreatePlaidItemResponse(
    val name:String,
    val accounts:List<Account>
){
    data class Account(
        val mask:String,
        val name:String,
        val plaidAccountId:String
    )
}
