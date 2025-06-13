package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.domain.repoi.CategoriesRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val categoriesRepo: CategoriesRepo) {
    suspend operator fun invoke(): Flow<ApiResult<List<CategoriesEntity>>> = categoriesRepo.fetchCategories()
}