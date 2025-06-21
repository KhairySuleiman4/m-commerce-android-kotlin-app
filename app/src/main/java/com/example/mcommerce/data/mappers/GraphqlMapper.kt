package com.example.mcommerce.data.mappers

import com.example.mcommerce.AddCartDiscountMutation
import com.example.mcommerce.AddItemToCartMutation
import com.example.mcommerce.CreateCartMutation
import com.example.mcommerce.GetAllProductsQuery
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCartByIdQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetOrdersQuery
import com.example.mcommerce.GetProductByIdQuery
import com.example.mcommerce.GetProductsByBrandQuery
import com.example.mcommerce.RemoveItemFromCartMutation
import com.example.mcommerce.data.models.CartModel
import com.example.mcommerce.data.models.CategoriesModel
import com.example.mcommerce.data.models.CollectionsModel
import com.example.mcommerce.data.models.LineModel
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
            price = it.node.priceRange.maxVariantPrice.amount.toString(),
            variantId = it.node.variants.edges[0].node.id
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
        isFavorite = false
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
            brand = it.node.vendor,
            variantId = it.node.variants.edges[0].node.id
        )
    }
}

fun GetCartByIdQuery.Data.toModel(): CartModel = CartModel(
    id = this.cart?.id ?: "",
    checkout = this.cart?.checkoutUrl.toString(),
    subtotalAmount = (this.cart?.cost?.subtotalAmount?.amount ?: "0.0").toString().toDouble(),
    totalAmount = (this.cart?.cost?.totalAmount?.amount ?: "0.0").toString().toDouble(),
    discountAmount = this.cart?.discountAllocations?.let {
        if (it.isNotEmpty())
            it[0].discountedAmount.amount.toString().toDouble()
        else
            0.0
    } ?: 0.0,
    items = this.cart?.lines?.edges?.map { it.toModel() } ?: listOf()
)

fun CreateCartMutation.Data.toModel(): CartModel = CartModel(
    id = this.cartCreate?.cart?.id ?: "",
    checkout = this.cartCreate?.cart?.checkoutUrl.toString(),
    subtotalAmount = (this.cartCreate?.cart?.cost?.subtotalAmount?.amount ?: "0.0").toString().toDouble(),
    totalAmount = (this.cartCreate?.cart?.cost?.totalAmount?.amount ?: "0.0").toString().toDouble(),
    discountAmount = this.cartCreate?.cart?.discountAllocations?.let {
        if (it.isNotEmpty())
            it[0].discountedAmount.amount.toString().toDouble()
        else
            0.0
    } ?: 0.0,
    items = this.cartCreate?.cart?.lines?.edges?.map { it.toModel() } ?: listOf()
)

fun AddItemToCartMutation.Data.toModel(): CartModel = CartModel(
    id = this.cartLinesAdd?.cart?.id ?: "",
    checkout = this.cartLinesAdd?.cart?.checkoutUrl.toString(),
    subtotalAmount = (this.cartLinesAdd?.cart?.cost?.subtotalAmount?.amount ?: "0.0").toString().toDouble(),
    totalAmount = (this.cartLinesAdd?.cart?.cost?.totalAmount?.amount ?: "0.0").toString().toDouble(),
    discountAmount = this.cartLinesAdd?.cart?.discountAllocations?.let {
        if (it.isNotEmpty())
            it[0].discountedAmount.amount.toString().toDouble()
        else
            0.0
    } ?: 0.0,
    items = this.cartLinesAdd?.cart?.lines?.edges?.map { it.toModel() } ?: listOf()
)

fun RemoveItemFromCartMutation.Data.toModel(): CartModel = CartModel(
    id = this.cartLinesRemove?.cart?.id ?: "",
    checkout = this.cartLinesRemove?.cart?.checkoutUrl.toString(),
    subtotalAmount = (this.cartLinesRemove?.cart?.cost?.subtotalAmount?.amount ?: "0.0").toString().toDouble(),
    totalAmount = (this.cartLinesRemove?.cart?.cost?.totalAmount?.amount ?: "0.0").toString().toDouble(),
    discountAmount = this.cartLinesRemove?.cart?.discountAllocations?.let {
        if (it.isNotEmpty())
            it[0].discountedAmount.amount.toString().toDouble()
        else
            0.0
    } ?: 0.0,
    items = this.cartLinesRemove?.cart?.lines?.edges?.map { it.toModel() } ?: listOf()
)

fun AddCartDiscountMutation.Data.toModel(): CartModel = CartModel(
    id = this.cartDiscountCodesUpdate?.cart?.id ?: "",
    checkout = this.cartDiscountCodesUpdate?.cart?.checkoutUrl.toString(),
    subtotalAmount = (this.cartDiscountCodesUpdate?.cart?.cost?.subtotalAmount?.amount ?: "0.0").toString().toDouble(),
    totalAmount = (this.cartDiscountCodesUpdate?.cart?.cost?.totalAmount?.amount ?: "0.0").toString().toDouble(),
    discountAmount = this.cartDiscountCodesUpdate?.cart?.discountAllocations?.let {
        if (it.isNotEmpty())
            it[0].discountedAmount.amount.toString().toDouble()
        else
            0.0
    } ?: 0.0,
    items = this.cartDiscountCodesUpdate?.cart?.lines?.edges?.map { it.toModel() } ?: listOf()
)

fun GetCartByIdQuery.Edge.toModel(): LineModel = LineModel(
    id = this.node.merchandise.onProductVariant?.id ?: "",
    quantity = this.node.quantity,
    price = (this.node.merchandise.onProductVariant?.price?.amount ?: "0.0").toString().toDouble(),
    image = this.node.merchandise.onProductVariant?.product?.featuredImage?.url.toString(),
    title = this.node.merchandise.onProductVariant?.product?.title ?: "",
    category = this.node.merchandise.onProductVariant?.title ?: "",
)

fun CreateCartMutation.Edge.toModel(): LineModel = LineModel(
    id = this.node.merchandise.onProductVariant?.id ?: "",
    quantity = this.node.quantity,
    price = (this.node.merchandise.onProductVariant?.price?.amount ?: "0.0").toString().toDouble(),
    image = this.node.merchandise.onProductVariant?.product?.featuredImage?.url.toString(),
    title = this.node.merchandise.onProductVariant?.product?.title ?: "",
    category = this.node.merchandise.onProductVariant?.title ?: "",
)

fun AddItemToCartMutation.Edge.toModel(): LineModel = LineModel(
    id = this.node.merchandise.onProductVariant?.id ?: "",
    quantity = this.node.quantity,
    price = (this.node.merchandise.onProductVariant?.price?.amount ?: "0.0").toString().toDouble(),
    image = this.node.merchandise.onProductVariant?.product?.featuredImage?.url.toString(),
    title = this.node.merchandise.onProductVariant?.product?.title ?: "",
    category = this.node.merchandise.onProductVariant?.title ?: "",
)

fun RemoveItemFromCartMutation.Edge.toModel(): LineModel = LineModel(
    id = this.node.merchandise.onProductVariant?.id ?: "",
    quantity = this.node.quantity,
    price = (this.node.merchandise.onProductVariant?.price?.amount ?: "0.0").toString().toDouble(),
    image = this.node.merchandise.onProductVariant?.product?.featuredImage?.url.toString(),
    title = this.node.merchandise.onProductVariant?.product?.title ?: "",
    category = this.node.merchandise.onProductVariant?.title ?: "",
)

fun AddCartDiscountMutation.Edge.toModel(): LineModel = LineModel(
    id = this.node.merchandise.onProductVariant?.id ?: "",
    quantity = this.node.quantity,
    price = (this.node.merchandise.onProductVariant?.price?.amount ?: "0.0").toString().toDouble(),
    image = this.node.merchandise.onProductVariant?.product?.featuredImage?.url.toString(),
    title = this.node.merchandise.onProductVariant?.product?.title ?: "",
    category = this.node.merchandise.onProductVariant?.title ?: "",
)

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
