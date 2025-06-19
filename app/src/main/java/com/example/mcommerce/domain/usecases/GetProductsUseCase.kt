package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.repoi.ProductsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val productsRepo: ProductsRepo) {
    suspend operator fun invoke(id: String): Flow<ApiResult<List<ProductsEntity>>> = productsRepo.fetchProductsByCollection(id)
}