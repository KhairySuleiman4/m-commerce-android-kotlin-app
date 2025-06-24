package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.repoi.ProductsRepo
import com.example.mcommerce.type.ProductSortKeys
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBestSellersUseCase @Inject constructor(
    private val productsRepo: ProductsRepo
) {
    suspend operator fun invoke(
        sortKeys: ProductSortKeys,
        reverse: Boolean
    ): Flow<ApiResult<List<ProductsEntity>>> = productsRepo.fetchHomeProducts(sortKeys, reverse)
}