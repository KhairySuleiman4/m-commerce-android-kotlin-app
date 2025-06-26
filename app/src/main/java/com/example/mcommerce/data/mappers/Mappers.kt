package com.example.mcommerce.data.mappers

import android.location.Address
import com.example.mcommerce.data.models.AddressModel
import com.example.mcommerce.data.models.CartModel
import com.example.mcommerce.data.models.CategoriesModel
import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.ExchangeResponse
import com.example.mcommerce.data.models.LineItemModel
import com.example.mcommerce.data.models.LineModel
import com.example.mcommerce.data.models.OrderModel
import com.example.mcommerce.data.models.ProductsForSearchModel
import com.example.mcommerce.data.models.ProductsModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.entities.CartEntity
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.CustomerEntity
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.domain.entities.LineItemEntity
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.entities.UserCredentialsEntity

fun CollectionsModel.toEntity(): CollectionsEntity {
    return CollectionsEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl
    )
}


fun ExchangeResponse.toEntity(): ExchangeRateEntity = ExchangeRateEntity(rates)

fun ProductsModel.toEntity(): ProductsEntity {
    return ProductsEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price,
        brand = this.brand,
        isFavorite = false
    )
}

fun CategoriesModel.toEntity(): CategoriesEntity {
    return CategoriesEntity(
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

fun Address.toModel(): AddressModel {
    return AddressModel(
        id = "",
        name = this.featureName,
        subName = "",
        country = this.countryName,
        zip = this.postalCode,
        latitude = this.latitude,
        longitude = this.longitude,
        city = this.adminArea
    )
}

fun AddressModel.toEntity(): AddressEntity {
    return AddressEntity(
        id = this.id,
        customerName = this.customerName,
        name = this.name,
        subName = this.subName,
        country = this.country,
        city = this.city,
        zip = this.zip,
        latitude = this.latitude,
        longitude = this.longitude,
        isDefault = this.isDefault
    )
}

fun AddressEntity.toModel(): AddressModel {
    return AddressModel(
        id = this.id,
        customerName = this.customerName,
        name = this.name,
        subName = this.subName,
        country = this.country,
        city = this.city,
        zip = this.zip,
        latitude = this.latitude,
        longitude = this.longitude,
    )
}


fun ProductsForSearchModel.toEntity(): ProductSearchEntity {
    return ProductSearchEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        productType = this.productType,
        price = this.price,
        brand = this.brand,
    )
}

fun CartModel.toEntity(): CartEntity = CartEntity(
    this.id,
    this.checkout,
    this.subtotalAmount,
    this.totalAmount,
    this.discountAmount,
    this.items.map { it.toEntity() },
    this.code
)

fun LineModel.toEntity(): LineEntity = LineEntity(
    this.id,
    this.quantity,
    this.price,
    this.image,
    this.title,
    this.category,
    this.brand,
    this.lineId
)

fun OrderModel.toEntity(): OrderEntity {
    return OrderEntity(
        name = this.name,
        processedAt = this.processedAt,
        subtotalPrice = this.subtotalPrice,
        totalPrice = this.totalPrice,
        shippingAddress = this.shippingAddress,
        city = this.city,
        customerName = this.customerName,
        phone = this.phone,
        lineItems = this.lineItems.map { it.toEntity() }
    )
}

fun LineItemModel.toEntity(): LineItemEntity {
    return LineItemEntity(
        quantity = this.quantity,
        variantTitle = this.variantTitle,
        productId = this.productId,
        productTitle = this.productTitle,
        price = this.price,
        imageUrl = this.imageUrl
    )
}

fun ProductInfoEntity.toSearchEntity(): ProductSearchEntity {
    return ProductSearchEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.images[0],
        productType = this.productType,
        price = this.price,
        brand = this.vendor,
    )
}