package tech.alexib.yaba.android.ui.transactions

import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.first
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.model.Transaction

class TransactionListPager(
    private val transactionRepository: TransactionRepository,
    private val log: Kermit
) : PagingSource<Int, Transaction>() {

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {

        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        try {
            val nextPageNumber = params.key ?: 1

            val response = transactionRepository.getAllPaged(50, nextPageNumber * 50L).first()
            return LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if(response.isNotEmpty()) nextPageNumber.plus(1) else null
            )
        } catch (e: Throwable) {
            log.e(e) { "Error fetching transactions" }
            throw e
        }
    }
}
