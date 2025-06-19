package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.repoi.ProductsRepo
import javax.inject.Inject

class InsertProductToFavoritesUseCase @Inject constructor(
    private val repo: ProductsRepo
) {
    suspend operator fun invoke(product: ProductSearchEntity) = repo.insertProductToFavorites(product)
}