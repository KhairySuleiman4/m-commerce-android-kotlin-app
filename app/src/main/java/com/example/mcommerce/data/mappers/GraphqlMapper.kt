package com.example.mcommerce.data.mappers

import com.example.mcommerce.GetAllProductsQuery
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetOrdersQuery
import com.example.mcommerce.GetProductByIdQuery
import com.example.mcommerce.GetProductsByBrandQuery
import com.example.mcommerce.data.models.CategoriesModel
import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.OrderModel
import com.example.mcommerce.data.models.ProductsForSearchModel
import com.example.mcommerce.data.models.ProductsModel
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductVariantEntity

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

fun GetProductByIdQuery.Data.toModel(): ProductInfoEntity{
    val id = this.current?.id ?: ""
    val images = this.current?.images?.nodes?.map { it.url.toString() } ?: listOf()
    val title = this.current?.title ?: ""
    val price = this.current?.priceRange?.maxVariantPrice?.amount.toString()
    val priceUnit = this.current?.priceRange?.maxVariantPrice?.currencyCode.toString()
    val productType = this.current?.productType ?: "Unknown Type"
    val vendor = this.current?.vendor ?: "Vendor N/A"
    val description = this.current?.description ?: "No Description"
    val variants: List<ProductVariantEntity> = this.current?.variants?.toListOfVariantEntity() ?: listOf()

    return ProductInfoEntity(
        id = id,
        images = images,
        title = title,
        price = price.toDouble(),
        priceUnit = priceUnit,
        productType = productType,
        vendor = vendor,
        description = description,
        variants = variants,
    )
}

fun GetProductByIdQuery.Variants.toListOfVariantEntity(): List<ProductVariantEntity>{
    return this.edges.map{
            it.node.toVariantEntity()
        }
}

fun GetProductByIdQuery.Node1.toVariantEntity(): ProductVariantEntity{
    return ProductVariantEntity(
        id = this.id,
        imageUrl = this.image?.url.toString(),
        title = this.title,
        price = this.price.amount.toString(),
    )
}

fun GetAllProductsQuery.Data.toModel(): List<ProductsForSearchModel>{
    return this.products.edges.map{
        ProductsForSearchModel(
            id = it.node.id,
            title = it.node.title,
            imageUrl = it.node.featuredImage?.url.toString(),
            productType = it.node.productType,
            price = it.node.priceRange.maxVariantPrice.amount.toString().toDouble(),
            brand = it.node.vendor
        )
    }
}

//fun GetOrdersQuery.Data.toModel(): List<OrderModel>?{
//    return this.customer?.orders?.edges?.map{
//        OrderModel(
//            orderId = it.node.id,
//            orderName = it.node.name,
//            orderDate = it.node.processedAt.toString(),
//            orderTime = it.node.processedAt.toString(),
//            orderPrice = it.node.totalPrice.amount.toString(),
//            currencyCode = it.node.totalPrice.currencyCode.toString(),
//            productTitle = it.node.lineItems.edges.,
//            productQuantity = TODO(),
//            productPrice = TODO(),
//            productImage = TODO(),
//            variantTitle = TODO(),
//            variantImage = TODO(),
//            customerUrl = TODO(),
//            customerName = TODO(),
//            customerPhone = TODO(),
//            shippingAddress = TODO(),
//            shippingCity = TODO()
//        )
//    }
//}
