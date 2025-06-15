package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.repoi.ProductsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repo: ProductsRepo
) {
    suspend operator fun invoke(id: String): Flow<ApiResult<ProductInfoEntity?>> = repo.fetchProductById(id)
}