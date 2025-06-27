package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.mappers.toCustomerEntity
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.auth.shopify.CustomerRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class AuthenticationRepoImpTest {
    private lateinit var repo: AuthenticationRepoImp

    private val firebase: Firebase = mockk()
    private val customerRemoteDataSource: CustomerRemoteDataSource = mockk()

    // Given
    private val dummyCredential = UserCredentialsEntity(
        name = "Test User",
        mail = "test@example.com",
        phoneNumber = "+201012345678",
        password = "password123",
        isVerified = false,
        accessToken = ""
    )

    @Before
    fun setUp() {
        repo = AuthenticationRepoImp(firebase, customerRemoteDataSource)
    }

    @Test
    fun createAccountOnFirebase_dummyCredentials_success() = runTest {
        // Given
        coEvery { firebase.createNewAccount(dummyCredential) } returns flowOf(ApiResult.Success(true))
        // When
        val result = repo.createAccountOnFirebase(dummyCredential).first()
        // Then
        assertThat(result, `is`(ApiResult.Success(true)))
    }

    @Test
    fun createAccountOnShopify_dummyCredentials_successWithCustomerId() = runTest {
        // Given
        val expectedId = "customer_id_123"
        coEvery {
            customerRemoteDataSource.createCustomer(dummyCredential.toCustomerEntity())
        } returns flowOf(ApiResult.Success(expectedId))

        // When
        val result = repo.createAccountOnShopify(dummyCredential).first()

        // Then
        assertThat(result, `is`(ApiResult.Success(expectedId)))
    }

    @Test
    fun updateNameOnAccount_dummyName_successWithUpdatedName() = runTest {
        // Given
        val newName = "Ahmad Muhammad"
        every { firebase.updateName(newName) } returns flowOf(ApiResult.Success(newName))

        // When
        val result = repo.updateNameOnAccount(newName).first()

        // Then
        assertThat(result, `is`(ApiResult.Success(newName)))
    }
}