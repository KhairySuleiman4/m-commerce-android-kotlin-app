package com.example.mcommerce.data.mappers

import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.data.models.CollectionsModel

fun GetBrandsQuery.Data.toModel(): List<CollectionsModel>{
    return this.collections.edges.map {
        CollectionsModel(
            id = it.node.id,
            title = it.node.title,
            imageUrl = it.node.image?.url.toString(),
            )
    }
}