package com.example.mcommerce.data.mappers

import com.example.mcommerce.data.models.CategoriesModel
import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.ExchangeResponse
import com.example.mcommerce.data.models.ProductsModel
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.entities.ProductsEntity

fun CollectionsModel.toEntity(): CollectionsEntity{
    return CollectionsEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl
    )
}

fun CollectionsEntity.toModel(): CollectionsModel{
    return CollectionsModel(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl
    )
}

fun ExchangeResponse.toEntity(): ExchangeRateEntity = ExchangeRateEntity(rates)

fun ExchangeRateEntity.toModel(): ExchangeResponse = ExchangeResponse(rates)

fun ProductsModel.toEntity(): ProductsEntity{
    return ProductsEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price
    )
}

fun ProductsEntity.toModel(): ProductsModel{
    return ProductsModel(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price
    )
}

fun CategoriesModel.toEntity(): CategoriesEntity{
    return CategoriesEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
    )
}

fun CategoriesEntity.toModel(): CategoriesModel{
    return CategoriesModel(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
    )
}