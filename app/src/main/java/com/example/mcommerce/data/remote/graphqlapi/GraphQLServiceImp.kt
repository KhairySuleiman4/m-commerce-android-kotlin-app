package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
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
import com.example.mcommerce.type.CartLineUpdateInput
import com.example.mcommerce.type.CustomerAccessTokenCreateInput
import com.example.mcommerce.type.CustomerCreateInput
import com.example.mcommerce.type.MailingAddressInput
import com.example.mcommerce.type.ProductSortKeys

class GraphQLServiceImp(private val client: ApolloClient) : GraphQLService {

    override suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data> =
        client.query(GetBrandsQuery()).execute()

    override suspend fun getProducts(id: String): ApolloResponse<GetProductsByBrandQuery.Data> =
        client.query(GetProductsByBrandQuery(Optional.present(id))).execute()

    override suspend fun getCategories(): ApolloResponse<GetCategoriesQuery.Data> =
        client.query(GetCategoriesQuery()).execute()

    override suspend fun getProductById(id: String): ApolloResponse<GetProductByIdQuery.Data> =
        client.query(GetProductByIdQuery(Optional.present(id))).execute()

    override suspend fun createCustomer(customer: CustomerEntity): ApolloResponse<CustomerCreateMutation.Data> =
        client.mutation(
            CustomerCreateMutation(
                CustomerCreateInput(
                    firstName = Optional.present(customer.name),
                    phone = Optional.present(customer.phone),
                    password = customer.password,
                    email = customer.email
                )
            )
        ).execute()

    override suspend fun createCustomerAccessToken(customer: CustomerEntity): ApolloResponse<CustomerAccessTokenCreateMutation.Data> =
        client.mutation(
            CustomerAccessTokenCreateMutation(
                CustomerAccessTokenCreateInput(
                    customer.email,
                    customer.password
                )
            )
        ).execute()

    override suspend fun getAllProducts(): ApolloResponse<GetAllProductsQuery.Data> =
        client.query(GetAllProductsQuery()).execute()

    override suspend fun getOrders(userAccessToken: String): ApolloResponse<GetOrdersQuery.Data> =
        client.query(GetOrdersQuery(userAccessToken)).execute()

    override suspend fun getCartById(id: String): ApolloResponse<GetCartByIdQuery.Data> =
        client.query(GetCartByIdQuery(id)).execute()

    override suspend fun createCart(
        accessToken: String,
        email: String
    ): ApolloResponse<CreateCartMutation.Data> =
        client.mutation(CreateCartMutation(customerToken = accessToken, email = email)).execute()

    override suspend fun addItemToCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): ApolloResponse<AddItemToCartMutation.Data> =
        client.mutation(AddItemToCartMutation(cartId, quantity, itemId)).execute()

    override suspend fun removeItemFromCart(
        cartId: String,
        itemId: String
    ): ApolloResponse<RemoveItemFromCartMutation.Data> =
        client.mutation(RemoveItemFromCartMutation(cartId, listOf(itemId))).execute()

    override suspend fun changeQuantityOfItemInCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): ApolloResponse<UpdateItemCountMutation.Data> = client.mutation(
        UpdateItemCountMutation(
            cartId,
            listOf(CartLineUpdateInput(itemId, quantity = Optional.present(quantity)))
        )
    ).execute()

    override suspend fun addDiscountCodeToCart(
        cartId: String,
        code: String
    ): ApolloResponse<AddCartDiscountMutation.Data> =
        client.mutation(AddCartDiscountMutation(cartId, listOf(code))).execute()

    override suspend fun getHomeProducts(
        sortKey: ProductSortKeys,
        reverse: Boolean
    ): ApolloResponse<GetHomeProductsQuery.Data> =
        client.query(GetHomeProductsQuery(Optional.present(sortKey), Optional.present(reverse)))
            .execute()

    override suspend fun getAddresses(accessToken: String): ApolloResponse<GetAddressesQuery.Data> =
        client.query(GetAddressesQuery(accessToken)).execute()

    override suspend fun addAddress(
        accessToken: String,
        address: AddressModel,
        name: String
    ): ApolloResponse<CustomerAddressCreateMutation.Data> = client.mutation(
        CustomerAddressCreateMutation(
            MailingAddressInput(
                lastName = Optional.present(name),
                address1 = Optional.present(address.name),
                address2 = Optional.present(address.subName),
                country = Optional.present(address.country),
                zip = Optional.present(address.zip),
                city = Optional.present(address.city)
            ),
            customerAccessToken = accessToken
        )
    ).execute()

    override suspend fun removeAddress(
        accessToken: String,
        addressId: String
    ): ApolloResponse<CustomerAddressDeleteMutation.Data> =
        client.mutation(CustomerAddressDeleteMutation(accessToken, addressId)).execute()

    override suspend fun changeDefaultAddress(
        accessToken: String,
        addressId: String
    ): ApolloResponse<CustomerDefaultAddressUpdateMutation.Data> =
        client.mutation(CustomerDefaultAddressUpdateMutation(addressId, accessToken)).execute()

    override suspend fun checkForDefaultAddress(accessToken: String): ApolloResponse<CheckForDefaultAddressQuery.Data> =
        client.query(CheckForDefaultAddressQuery(accessToken)).execute()

}