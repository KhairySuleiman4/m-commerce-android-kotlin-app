package com.example.mcommerce.presentation.utils

import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.presentation.products.ProductsContract

fun ProductsContract.ProductUIModel.toSearchEntity(): ProductSearchEntity {
    return ProductSearchEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price.toDouble(),
        brand = "TODO",
        isFavorite = this.isFavorite,
        variantId = this.variantId
    )
}