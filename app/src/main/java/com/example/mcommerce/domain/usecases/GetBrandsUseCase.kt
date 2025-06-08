package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.repoi.AppRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBrandsUseCase @Inject constructor(private val repo: AppRepo) {
    suspend operator fun invoke(): Flow<ApiResult<List<CollectionsEntity>>> = repo.fetchBrands()
}