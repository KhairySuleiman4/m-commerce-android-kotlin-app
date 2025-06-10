package com.example.mcommerce.data.mappers

import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.ExchangeResponse
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.ExchangeRateEntity

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