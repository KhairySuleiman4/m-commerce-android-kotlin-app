package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.repoi.BrandsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBrandsUseCase @Inject constructor(private val brandsRepo: BrandsRepo) {
    suspend operator fun invoke(): Flow<ApiResult<List<CollectionsEntity>>> =
        brandsRepo.fetchBrands()
}