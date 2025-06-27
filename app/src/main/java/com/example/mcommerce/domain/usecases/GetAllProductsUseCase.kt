package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.repoi.ProductsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(
    private val productsRepo: ProductsRepo
) {
    suspend operator fun invoke(): Flow<ApiResult<List<ProductSearchEntity>>> =
        productsRepo.fetchAllProducts()
}