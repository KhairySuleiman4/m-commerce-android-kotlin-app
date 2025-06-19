package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.local.cartcache.CartCache
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.cart.CartRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import com.example.mcommerce.domain.repoi.CartRepo
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class CartRepoImp(
    private val remoteDataSource: CartRemoteDataSource,
    private val firebase: Firebase,
    private val cache: CartCache
) : CartRepo{
    override fun getCart(): Flow<ApiResult<CartEntity?>> = flow {
        if (!firebase.isMeLoggedIn()) {
            emit(ApiResult.Failure(Throwable("You are not logged in")))
            return@flow
        }

        if (!firebase.isUserVerified()) {
            emit(ApiResult.Failure(Throwable("Your account is not Verified")))
            return@flow
        }

        cache.getCart()?.let {
            emit(ApiResult.Success(it))
            return@flow
        }

        val customerInfo = firebase.getCustomerAccessToken()
        val json = Gson().fromJson(customerInfo, JsonObject::class.java)
        val cartId = json.get("cart")?.asString
        val access = json.get("token")?.asString
        val email = firebase.getEmail()
        if (!cartId.isNullOrBlank()) {
            emit(ApiResult.Loading())
            remoteDataSource.getCartById(cartId).collect { result ->
                when(result){
                    is ApiResult.Failure ->{
                        emit(result)
                        return@collect
                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        cache.setCart(result.data)
                        emit(result)
                        return@collect
                    }
                }
            }
        } else {
            emit(ApiResult.Loading())
            remoteDataSource.createCart(access.toString(),email).collect{
                when(it){
                    is ApiResult.Failure -> {
                        emit(it)
                        return@collect
                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        cache.setCart(it.data)
                        json.addProperty("cart",it.data?.id)
                        firebase.updatePhoto(json.toString()).collect{ result ->
                            when(result){
                                is ApiResult.Failure -> {

                                }
                                is ApiResult.Loading -> {
                                }
                                is ApiResult.Success -> {
                                    emit(it)
                                }
                            }
                        }
                        return@collect
                    }
                }
            }
        }
    }

    override fun addItemToCart(itemId: String, quantity: Int): Flow<ApiResult<CartEntity?>> = flow{
        if (!firebase.isMeLoggedIn()){
            emit(ApiResult.Failure(Throwable(message = "You are not logged in")))
            return@flow
        }
        if (!firebase.isUserVerified()) {
            emit(ApiResult.Failure(Throwable("Your account is not Verified")))
            return@flow
        }
        val cart = cache.getCart()
        if (cart!=null) {
            if (cart.items.any { it.id == itemId }){
                emit(ApiResult.Failure(Throwable("The item already added")))
                return@flow
            }
            emit(ApiResult.Loading())
            remoteDataSource.addItemToCart(cartId = cart.id, quantity, itemId).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        emit(it)
                        return@collect
                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        cache.setCart(it.data)
                        emit(it)
                        return@collect
                    }
                }
            }
        }
        else {

            getCart()
                .filterIsInstance<ApiResult.Success<CartEntity?>>()
                .firstOrNull()
                ?.data
                ?.let { freshCart ->
                    if (freshCart.items.any { it.id == itemId }){
                        emit(ApiResult.Failure(Throwable("The item already added")))
                        return@flow
                    }
                    emit(ApiResult.Loading())
                    remoteDataSource.addItemToCart(cartId = freshCart.id, quantity, itemId)
                        .collect {
                            when (it) {
                                is ApiResult.Failure -> {
                                    emit(it)
                                    return@collect
                                }

                                is ApiResult.Loading -> {

                                }

                                is ApiResult.Success -> {
                                    cache.setCart(it.data)
                                    emit(it)
                                    return@collect
                                }
                            }
                        }

                }
                ?: emit(ApiResult.Failure(Throwable("Unable to obtain cart")))
            return@flow
        }

    }

    override fun removeItemFromCart(itemId: String): Flow<ApiResult<CartEntity?>> = flow{
        if (!firebase.isMeLoggedIn()){
            emit(ApiResult.Failure(Throwable(message = "You are not logged in")))
            return@flow
        }
        if (!firebase.isUserVerified()) {
            emit(ApiResult.Failure(Throwable("Your account is not Verified")))
            return@flow
        }
        val cart = cache.getCart()
        if (cart!=null){
            if (!cart.items.any { it.id == itemId }){
                emit(ApiResult.Failure(Throwable("No item Found")))
                return@flow
            }
                emit(ApiResult.Loading())
                remoteDataSource.removeItemFromCart(cartId = cart.id,itemId).collect {
                    when (it) {
                        is ApiResult.Failure -> {
                            emit(it)
                            return@collect
                        }

                        is ApiResult.Loading -> {

                        }

                        is ApiResult.Success -> {
                            cache.setCart(it.data)
                            emit(it)
                            return@collect
                        }
                    }
                }
            }
        else{
            getCart()
            .filterIsInstance<ApiResult.Success<CartEntity?>>()
            .firstOrNull()
            ?.data
            ?.let { freshCart ->
                if (!freshCart.items.any { it.id == itemId }){
                    emit(ApiResult.Failure(Throwable("No item Found")))
                    return@flow
                }
                    emit(ApiResult.Loading())
                    remoteDataSource.removeItemFromCart(freshCart.id, itemId).collect {
                        when (it) {
                            is ApiResult.Failure -> {
                                emit(it)
                                return@collect
                            }

                            is ApiResult.Loading -> {

                            }

                            is ApiResult.Success -> {
                                cache.setCart(it.data)
                                emit(it)
                                return@collect
                            }
                        }
                    }
                }
                ?: emit(ApiResult.Failure(Throwable("Unable to obtain cart")))
            return@flow
            }
    }

    override suspend fun changeItem(itemId: String, quantity: Int): Flow<ApiResult<Boolean>> {
        if (!firebase.isMeLoggedIn()){
            return flow { emit(ApiResult.Failure(Throwable(message = "You are not logged in"))) }
        }
        if (!firebase.isUserVerified()) {
            return flow { emit(ApiResult.Failure(Throwable("Your account is not Verified"))) }
        }
        val cart = cache.getCart()
        if (cart!=null)
            return remoteDataSource.changeQuantityOfItemInCart(cartId = cart.id,quantity,itemId)
        return getCart()
            .filterIsInstance<ApiResult.Success<CartEntity?>>()
            .firstOrNull()
            ?.data
            ?.let { freshCart ->
                remoteDataSource.changeQuantityOfItemInCart(freshCart.id, quantity, itemId)
            }
            ?: flow {
                emit(ApiResult.Failure(Throwable("Unable to obtain cart")))
            }
    }

    override fun addDiscountCode(code: String): Flow<ApiResult<CartEntity?>> = flow{
        if (!firebase.isMeLoggedIn()){
            emit(ApiResult.Failure(Throwable(message = "You are not logged in")))
            return@flow
        }
        if (!firebase.isUserVerified()) {
            emit(ApiResult.Failure(Throwable("Your account is not Verified")))
            return@flow
        }
        val cart = cache.getCart()
        if (cart!=null){
            emit(ApiResult.Loading())
            remoteDataSource.addDiscountCodeToCart(cartId = cart.id,code).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        emit(it)
                        return@collect
                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        cache.setCart(it.data)
                        emit(it)
                        return@collect
                    }
                }
            }
        }
        else{
            getCart()
                .filterIsInstance<ApiResult.Success<CartEntity?>>()
                .firstOrNull()
                ?.data
                ?.let { freshCart ->
                    emit(ApiResult.Loading())
                    remoteDataSource.addDiscountCodeToCart(freshCart.id, code).collect {
                        when (it) {
                            is ApiResult.Failure -> {
                                emit(it)
                                return@collect
                            }

                            is ApiResult.Loading -> {

                            }

                            is ApiResult.Success -> {
                                cache.setCart(it.data)
                                emit(it)
                                return@collect
                            }
                        }
                    }
                }
                ?: emit(ApiResult.Failure(Throwable("Unable to obtain cart")))
            return@flow
        }
    }

    override fun clearLocalCart(): Boolean {
        cache.clear()
        return true
    }

    override suspend fun removeCart(): Flow<ApiResult<Boolean>> {
        val customerInfo = firebase.getCustomerAccessToken()
        val json = Gson().fromJson(customerInfo, JsonObject::class.java)
        json.addProperty("cart","")
        return firebase.updatePhoto(json.toString())
    }
}