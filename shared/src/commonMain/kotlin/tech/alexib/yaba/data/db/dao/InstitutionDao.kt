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
package tech.alexib.yaba.data.db.dao

import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.InstitutionEntity
import tech.alexib.yaba.data.db.InstitutionEntityQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.model.Institution

internal interface InstitutionDao {
    suspend fun insert(institution: Institution)
//    suspend fun selectAll(): Flow<List<Institution>>
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
                    id = institution.institutionId,
                    logo = institution.logo,
                    name = institution.name,
                    primary_color = institution.primaryColor
                )
            )
        }
    }

//    override suspend fun selectAll(): Flow<List<Institution>> {
//        return queries.selectAll(institutionMapper).asFlow().mapToList()
//            .flowOn(backgroundDispatcher)
//    }

    private val institutionMapper = {
        id: String,
        logo: String,
        name: String,
        primary_color: String,
        ->
        Institution(
            institutionId = id,
            logo = logo,
            name = name,
            primaryColor = primary_color
        )
    }
}
