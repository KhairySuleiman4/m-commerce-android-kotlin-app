package com.example.mcommerce.presentation.utils

import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.presentation.products.ProductsContract

fun ProductsContract.ProductUIModel.toSearchEntity(): ProductSearchEntity {
    return ProductSearchEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price.toDouble(),
        brand = this.brand,
        isFavorite = this.isFavorite
    )
}

fun ProductsContract.ProductUIModel.toProductsEntity(): ProductsEntity{
    return ProductsEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        brand = this.brand,
        isFavorite = this.isFavorite,
        price = this.price
    )
}