package tech.alexib.yaba.kmm.data.db.dao

import co.touchlab.stately.ensureNeverFrozen
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.InstitutionEntity
import tech.alexib.yaba.data.db.InstitutionEntityQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.model.Institution
import tech.alexib.yaba.kmm.model.PlaidInstitutionId

internal interface InstitutionDao {
    suspend fun insert(institution: Institution)
    suspend fun selectAll(): Flow<List<Institution>>
}


internal class InstitutionDaoImpl(
    database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher
) : InstitutionDao {
    private val queries: InstitutionEntityQueries = database.institutionEntityQueries

    init {
        ensureNeverFrozen()

    }
    override suspend fun insert(institution: Institution) {
        withContext(backgroundDispatcher) {
            queries.insertInstitution(
                InstitutionEntity(
                    id = institution.institutionId.value,
                    logo = institution.logo,
                    name = institution.name,
                    primary_color = institution.primaryColor
                )
            )
        }
    }

    override suspend fun selectAll(): Flow<List<Institution>> {
        return queries.selectAll(institutionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }


    companion object {
        private val institutionMapper = {
                id: String,
                logo: String,
                name: String,
                primary_color: String,
            ->
            Institution(
                institutionId = PlaidInstitutionId(id),
                logo = logo,
                name = name,
                primaryColor = primary_color
            )
        }
    }
}
