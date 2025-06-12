package com.example.mcommerce.data.mappers

import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.ProductsModel
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.CustomerEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.entities.UserCredentialsEntity

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

fun ProductsModel.toEntity(): ProductsEntity{
    return ProductsEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType
    )
}

fun ProductsEntity.toModel(): ProductsModel{
    return ProductsModel(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType
    )
}


fun UserCredentialsEntity.toUrlField(): String {
    return """{ "token": "${this.accessToken}", "phone": "${this.phoneNumber}" }"""
}

fun UserCredentialsEntity.toCustomerEntity(): CustomerEntity {
    return CustomerEntity(
        name = this.name,
        email = this.mail,
        phone = this.phoneNumber,
        password = this.password,
    )
}