package com.example.mcommerce.data.remote.auth.firebase

import android.net.Uri
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseImp(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
): Firebase {
    override suspend fun createNewAccount(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>> =
        executeAPI {
            auth.createUserWithEmailAndPassword(credentials.mail, credentials.password).await()

            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(credentials.name)
                    .setPhotoUri(Uri.parse("""{"token":"${credentials.accessToken}"}"""))
                    .build()
            )?.await()

            auth.currentUser?.sendEmailVerification()?.await()

            true
        }

    override suspend fun login(email: String, password: String): Flow<ApiResult<Boolean>> =
        executeAPI {
            auth.signInWithEmailAndPassword(email, password).await()

            true
        }

    override suspend fun updatePhoto(value: String): Flow<ApiResult<Boolean>> =
        executeAPI {
            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(value))
                    .build()
            )?.await()
            auth.currentUser?.reload()?.await()
            true
        }

    override fun updateName(name: String): Flow<ApiResult<String>> = executeAPI {
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        )?.await()
        auth.currentUser?.reload()?.await()
        auth.currentUser?.displayName ?: ""
    }

    override fun isMeLoggedIn(): Boolean = auth.currentUser != null

    override fun logout() =
        auth.signOut()

    override fun isUserVerified(): Boolean = auth.currentUser?.isEmailVerified ?: false


    override suspend fun getCustomerAccessToken(): String {
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.photoUrl.toString()
    }
    override fun getEmail(): String = auth.currentUser?.email ?: ""

    override fun getName(): String = auth.currentUser?.displayName ?: ""

    override fun isGuestMode(): Boolean = auth.currentUser == null

    override suspend fun insertProductToFavorites(product: ProductSearchEntity) {
        val customerId = auth.currentUser?.uid
        val productId = product.id.replace("/", "_")
        if (customerId != null) {
            fireStore.collection("customers")
                .document(customerId)
                .collection("products")
                .document(productId)
                .set(product)
        }
    }

    override suspend fun getFavoriteProducts(): Flow<ApiResult<List<ProductSearchEntity>>> {
        val customerId = auth.currentUser?.uid
        return flow {
            emit(ApiResult.Loading())
            if(customerId != null){
                val snapshot = fireStore.collection("customers")
                    .document(customerId)
                    .collection("products")
                    .get()
                    .await()
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ProductSearchEntity::class.java)
                }
                emit(ApiResult.Success(products))
            }
        }.catch {
            emit(ApiResult.Failure(Exception("Failed to fetch favorite products: ${it.message}")))
        }
    }

    override suspend fun deleteProduct(id: String) {
        val customerId = auth.currentUser?.uid
        val productId = id.replace("/", "_")
        if (customerId != null){
            fireStore.collection("customers")
                .document(customerId)
                .collection("products")
                .document(productId)
                .delete()
                .await()
        }
    }

}