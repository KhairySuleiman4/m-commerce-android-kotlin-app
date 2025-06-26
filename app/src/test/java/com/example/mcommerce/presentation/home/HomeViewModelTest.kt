import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.usecases.GetBestSellersUseCase
import com.example.mcommerce.domain.usecases.GetBrandsUseCase
import com.example.mcommerce.domain.usecases.GetLatestArrivalsUseCase
import com.example.mcommerce.presentation.home.HomeContract
import com.example.mcommerce.presentation.home.HomeViewModel
import com.example.mcommerce.type.ProductSortKeys
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val brandsUseCase: GetBrandsUseCase = mockk()
    private val bestsellersUseCase: GetBestSellersUseCase = mockk()
    private val latestArrivalsUseCase: GetLatestArrivalsUseCase = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = HomeViewModel(
            brandsUseCase = brandsUseCase,
            homeProductsUseCase = bestsellersUseCase,
            latestArrivalsUseCase = latestArrivalsUseCase,
            getFavoriteProductsUseCase = mockk(relaxed = true),
            insertProductToFavoritesUseCase = mockk(relaxed = true),
            deleteFavoriteProductUseCase = mockk(relaxed = true),
            getDiscountCodesUseCase = mockk(relaxed = true),
            isGuestModeUseCase = mockk(relaxed = true),
            getCurrentCurrencyUseCase = mockk(relaxed = true),
            getCurrentExchangeRateUseCase = mockk(relaxed = true)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getHomeData_listOfCollectionsAndListOfBestsellersAndListOfLatestArrivals_updateStateWithSuccess() =
        runTest {
            // given
            val collectionsList = listOf(
                CollectionsEntity(
                    id = "1",
                    title = "Adidas",
                    imageUrl = "AdidasImage"
                ),
                CollectionsEntity(
                    id = "2",
                    title = "Nike",
                    imageUrl = "NikeImage"
                )
            )
            val bestsellerProducts = listOf(
                ProductsEntity(
                    id = "1",
                    title = "Adidas t-shirt",
                    imageUrl = "Adidas t-shirt image",
                    productType = "t-shirt",
                    brand = "Adidas",
                    isFavorite = false,
                    price = "0.0"
                ),
                ProductsEntity(
                    id = "2",
                    title = "Nike t-shirt",
                    imageUrl = "Nike t-shirt image",
                    productType = "t-shirt",
                    brand = "Nike",
                    isFavorite = false,
                    price = "0.0"
                )
            )
            val latestArrivalsProducts = listOf(
                ProductsEntity(
                    id = "1",
                    title = "Adidas t-shirt",
                    imageUrl = "Adidas t-shirt image",
                    productType = "t-shirt",
                    brand = "Adidas",
                    isFavorite = false,
                    price = "0.0"
                ),
                ProductsEntity(
                    id = "2",
                    title = "Nike t-shirt",
                    imageUrl = "Nike t-shirt image",
                    productType = "t-shirt",
                    brand = "Nike",
                    isFavorite = false,
                    price = "0.0"
                )
            )
            val flow = flowOf(ApiResult.Success(collectionsList))

            coEvery { brandsUseCase() } returns flow
            coEvery { bestsellersUseCase(ProductSortKeys.BEST_SELLING, false) } returns flowOf(
                ApiResult.Success(bestsellerProducts)
            )
            coEvery { latestArrivalsUseCase(ProductSortKeys.CREATED_AT, true) } returns flowOf(
                ApiResult.Success(latestArrivalsProducts)
            )

            // when
            viewModel.invokeActions(HomeContract.Action.LoadHomeData)
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            val state = viewModel.states.value
            assertEquals(false, state.brandsLoading)
            assertEquals(collectionsList, state.brandsList)
            assertEquals(false, state.bestSellersLoading)
            assertEquals(bestsellerProducts, state.bestSellersList)
            assertEquals(false, state.latestArrivalsLoading)
            assertEquals(latestArrivalsProducts, state.latestArrivals)
        }

}
