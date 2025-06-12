package com.example.mcommerce.data.mappers

import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetProductsByBrandQuery
import com.example.mcommerce.data.models.CategoriesModel
import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.ProductsModel

fun GetBrandsQuery.Data.toModel(): List<CollectionsModel>{
    return this.collections.edges.map {
        CollectionsModel(
            id = it.node.id,
            title = it.node.title,
            imageUrl = it.node.image?.url.toString(),
            )
    }
}

fun GetProductsByBrandQuery.Data.toModel(): List<ProductsModel>{
    return this.collection?.products?.edges?.map {
        ProductsModel(
            id = it.node.id,
            title = it.node.title,
            imageUrl = it.node.featuredImage?.url.toString(),
            productType = it.node.productType,
            price = it.node.priceRange.maxVariantPrice.amount.toString()
        )
    } ?: listOf()
}

fun GetCategoriesQuery.Data.toModel(): List<CategoriesModel>{
    return this.collections.edges.map{
        CategoriesModel(
            id = it.node.id,
            title = it.node.title,
            description = it.node.description,
            imageUrl = it.node.image?.url.toString(),
        )
    }
}