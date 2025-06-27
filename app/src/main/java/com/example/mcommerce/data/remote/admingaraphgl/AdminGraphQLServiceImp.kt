package com.example.mcommerce.data.remote.admingaraphgl

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.example.mcommerce.GetDiscountCodeQuery
import com.example.mcommerce.OrderCreateMutation
import com.example.mcommerce.data.models.LineModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.type.MailingAddressInput
import com.example.mcommerce.type.OrderCreateDiscountCodeInput
import com.example.mcommerce.type.OrderCreateOrderInput
import com.example.mcommerce.type.OrderCreatePercentageDiscountCodeAttributesInput
import com.example.mcommerce.type.OrderLineItemInput

class AdminGraphQLServiceImp(
    private val apolloClient: ApolloClient
) : AdminGraphQLService {
    override suspend fun getDiscountCodes(): ApolloResponse<GetDiscountCodeQuery.Data> =
        apolloClient.query(GetDiscountCodeQuery()).execute()

    override suspend fun createOrder(
        email: String,
        items: List<LineModel>,
        address: AddressEntity,
        code: String
    ): ApolloResponse<OrderCreateMutation.Data> =
        apolloClient.mutation(
            OrderCreateMutation(
                order = OrderCreateOrderInput(
                    email = Optional.present(email),
                    shippingAddress = Optional.present(
                        MailingAddressInput(
                            address1 = Optional.present(address.name),
                            address2 = Optional.present(address.subName),
                            city = Optional.present(address.city),
                            country = Optional.present(address.country),
                            zip = Optional.present(address.zip),
                            lastName = Optional.present(address.customerName),
                        )
                    ),
                    discountCode = if (code.isNotBlank())
                        Optional.present(
                            OrderCreateDiscountCodeInput(
                                itemPercentageDiscountCode = Optional.present(
                                    OrderCreatePercentageDiscountCodeAttributesInput(code = code, percentage = Optional.present(code.filter { x -> x.isDigit() }.toDouble()))
                                )
                            )
                        )
                    else Optional.absent(),
                    lineItems = items.map { item ->
                        OrderLineItemInput(
                            quantity = item.quantity,
                            variantId = Optional.present(item.id),
                            productId = item.productId
                        )
                    }
                )
            )
        ).execute()
}
