package com.example.mcommerce.data.remote.auth.firebase

import android.net.Uri
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FirebaseImp(
    private val auth: FirebaseAuth
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

    override fun isMeLoggedIn(): Boolean = auth.currentUser != null

    override fun logout() =
        auth.signOut()

    override fun isUserVerified(): Boolean = auth.currentUser?.isEmailVerified ?: false


    override suspend fun getCustomerAccessToken(): String {
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.photoUrl.toString()
    }
    override fun getEmail(): String = auth.currentUser?.email ?: ""


    override fun isGuestMode(): Boolean = auth.currentUser == null

}