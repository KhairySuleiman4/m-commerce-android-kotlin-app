package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.ProductsRepo
import javax.inject.Inject

class DeleteFavoriteProductUseCase @Inject constructor(
    private val repo: ProductsRepo
) {
    suspend operator fun invoke(id: String) = repo.deleteFavoriteProduct(id)
}