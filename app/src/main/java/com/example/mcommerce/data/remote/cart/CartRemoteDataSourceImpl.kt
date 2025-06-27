package com.example.mcommerce.data.remote.cart

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import kotlinx.coroutines.flow.Flow

class CartRemoteDataSourceImpl(private val graphQLService: GraphQLService) : CartRemoteDataSource {
    override fun getCartById(cartId: String): Flow<ApiResult<CartEntity?>> =
        executeAPI { graphQLService.getCartById(cartId).data?.toModel()?.toEntity() }

    override fun createCart(accessToken: String, email: String): Flow<ApiResult<CartEntity?>> =
        executeAPI {
            val x = graphQLService.createCart(accessToken, email).data
            x?.toModel()?.toEntity()
        }

    override fun addItemToCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): Flow<ApiResult<CartEntity?>> = executeAPI {
        graphQLService.addItemToCart(cartId, quantity, itemId).data?.toModel()?.toEntity()
    }

    override fun removeItemFromCart(cartId: String, itemId: String): Flow<ApiResult<CartEntity?>> =
        executeAPI { graphQLService.removeItemFromCart(cartId, itemId).data?.toModel()?.toEntity() }

    override fun changeQuantityOfItemInCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): Flow<ApiResult<CartEntity?>> = executeAPI {
        graphQLService.changeQuantityOfItemInCart(
            cartId,
            quantity,
            itemId
        ).data?.toModel()?.toEntity()
    }

    override fun addDiscountCodeToCart(cartId: String, code: String): Flow<ApiResult<CartEntity?>> =
        executeAPI {
            graphQLService.addDiscountCodeToCart(cartId, code).data?.toModel()?.toEntity()
        }
}