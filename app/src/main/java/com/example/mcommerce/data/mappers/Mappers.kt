package com.example.mcommerce.data.mappers

import android.location.Address
import com.example.mcommerce.data.models.AddressModel
import com.example.mcommerce.data.models.CategoriesModel
import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.ExchangeResponse
import com.example.mcommerce.data.models.ProductsForSearchModel
import com.example.mcommerce.data.models.ProductsModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.CustomerEntity
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
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

fun UserCredentialsEntity.toCustomerEntity(): CustomerEntity {
    return CustomerEntity(
        name = this.name,
        email = this.mail,
        phone = this.phoneNumber,
        password = this.password,
    )
}

fun Address.toModel(): AddressModel{
    return AddressModel(
        name = this.getAddressLine(0),
        country = this.countryName,
        zip = this.postalCode,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun AddressModel.toEntity(): AddressEntity{
    return AddressEntity(
        name = this.name,
        country = this.country,
        zip = this.zip,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun ProductsForSearchModel.toEntity(): ProductSearchEntity{
    return ProductSearchEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price,
        brand = this.brand
    )
}