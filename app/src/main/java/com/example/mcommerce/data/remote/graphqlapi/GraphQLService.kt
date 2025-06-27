package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.AddCartDiscountMutation
import com.example.mcommerce.AddItemToCartMutation
import com.example.mcommerce.CheckForDefaultAddressQuery
import com.example.mcommerce.CreateCartMutation
import com.example.mcommerce.CustomerAccessTokenCreateMutation
import com.example.mcommerce.CustomerAddressCreateMutation
import com.example.mcommerce.CustomerAddressDeleteMutation
import com.example.mcommerce.CustomerCreateMutation
import com.example.mcommerce.CustomerDefaultAddressUpdateMutation
import com.example.mcommerce.GetAddressesQuery
import com.example.mcommerce.GetAllProductsQuery
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCartByIdQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetHomeProductsQuery
import com.example.mcommerce.GetOrdersQuery
import com.example.mcommerce.GetProductByIdQuery
import com.example.mcommerce.GetProductsByBrandQuery
import com.example.mcommerce.RemoveItemFromCartMutation
import com.example.mcommerce.UpdateItemCountMutation
import com.example.mcommerce.data.models.AddressModel
import com.example.mcommerce.domain.entities.CustomerEntity
import com.example.mcommerce.type.ProductSortKeys

interface GraphQLService {
    suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data>
    suspend fun getProducts(id: String): ApolloResponse<GetProductsByBrandQuery.Data>
    suspend fun createCustomer(customer: CustomerEntity): ApolloResponse<CustomerCreateMutation.Data>
    suspend fun createCustomerAccessToken(customer: CustomerEntity): ApolloResponse<CustomerAccessTokenCreateMutation.Data>
    suspend fun getCategories(): ApolloResponse<GetCategoriesQuery.Data>
    suspend fun getProductById(id: String): ApolloResponse<GetProductByIdQuery.Data>
    suspend fun getAllProducts(): ApolloResponse<GetAllProductsQuery.Data>
    suspend fun getOrders(userAccessToken: String): ApolloResponse<GetOrdersQuery.Data>
    suspend fun getCartById(id: String): ApolloResponse<GetCartByIdQuery.Data>
    suspend fun createCart(
        accessToken: String,
        email: String
    ): ApolloResponse<CreateCartMutation.Data>

    suspend fun addItemToCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): ApolloResponse<AddItemToCartMutation.Data>

    suspend fun removeItemFromCart(
        cartId: String,
        itemId: String
    ): ApolloResponse<RemoveItemFromCartMutation.Data>

    suspend fun changeQuantityOfItemInCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): ApolloResponse<UpdateItemCountMutation.Data>

    suspend fun addDiscountCodeToCart(
        cartId: String,
        code: String
    ): ApolloResponse<AddCartDiscountMutation.Data>

    suspend fun getHomeProducts(
        sortKey: ProductSortKeys,
        reverse: Boolean
    ): ApolloResponse<GetHomeProductsQuery.Data>

    suspend fun getAddresses(accessToken: String): ApolloResponse<GetAddressesQuery.Data>
    suspend fun addAddress(
        accessToken: String,
        address: AddressModel,
        name: String
    ): ApolloResponse<CustomerAddressCreateMutation.Data>

    suspend fun removeAddress(
        accessToken: String,
        addressId: String
    ): ApolloResponse<CustomerAddressDeleteMutation.Data>

    suspend fun changeDefaultAddress(
        accessToken: String,
        addressId: String
    ): ApolloResponse<CustomerDefaultAddressUpdateMutation.Data>

    suspend fun checkForDefaultAddress(accessToken: String): ApolloResponse<CheckForDefaultAddressQuery.Data>
}