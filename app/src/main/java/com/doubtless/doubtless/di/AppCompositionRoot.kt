package com.doubtless.doubtless.di

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.localDatabase.AppDatabase
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.navigation.Router
import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.screens.answers.usecases.FetchAnswerUseCase
import com.doubtless.doubtless.screens.answers.usecases.PublishAnswerUseCase
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.usecases.UserDataServerUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserDataStorageUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.dashboard.usecases.DeleteAccountUseCase
import com.doubtless.doubtless.screens.dashboard.usecases.FetchUserDataUseCase
import com.doubtless.doubtless.screens.dashboard.usecases.FetchUserFeedByDateUseCase
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.usecases.DoubtDataSharedPrefUseCase
import com.doubtless.doubtless.screens.doubt.usecases.FetchDoubtDataFromDoubtIdUseCase
import com.doubtless.doubtless.screens.doubt.usecases.FetchFilterTagsUseCase
import com.doubtless.doubtless.screens.doubt.usecases.PostDoubtUseCase
import com.doubtless.doubtless.screens.doubt.usecases.VotingUseCase
import com.doubtless.doubtless.screens.home.entities.FeedConfig
import com.doubtless.doubtless.screens.home.usecases.FetchFeedByDateUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchFeedByPopularityUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.inAppNotification.dao.InAppNotificationDao
import com.doubtless.doubtless.screens.inAppNotification.usecases.FetchInAppNotificationUseCase
import com.doubtless.doubtless.screens.inAppNotification.usecases.FetchUnreadNotificationUseCase
import com.doubtless.doubtless.screens.inAppNotification.usecases.MarkInAppNotificationsReadUseCase
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.main.MainFragment
import com.doubtless.doubtless.screens.onboarding.usecases.AddOnBoardingDataUseCase
import com.doubtless.doubtless.screens.onboarding.usecases.FetchOnBoardingDataUseCase
import com.doubtless.doubtless.screens.search.usecases.ExtractKeywordsUseCase
import com.doubtless.doubtless.screens.search.usecases.FetchSearchResultsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppCompositionRoot(appContext: DoubtlessApp) {


    // ------- Analytics ---------

    private lateinit var analyticsTracker: AnalyticsTracker

    private val amplitude = Amplitude(
        Configuration(
            apiKey = "9ccdf7b8da7390a82fd779a2de0c6b1b", context = appContext
        )
    )

    @Synchronized
    // should be initialized after splash screen isLoggedIn check
    fun getAnalyticsTracker(): AnalyticsTracker {

        if (::analyticsTracker.isInitialized == false) {
            analyticsTracker = AnalyticsTracker(amplitude, getUserManager())
        }

        return analyticsTracker
    }

    // --------- HomeFeed ------------

    // should be initialized after splash screen isLoggedIn check
    fun getFetchHomeFeedUseCase(feedConfig: FeedConfig): FetchHomeFeedUseCase {
        return FetchHomeFeedUseCase(
            getFetchFeedByDataUseCase(),
            getFetchFeedByPopularityUseCase(),
            FirebaseFirestore.getInstance(),
            feedConfig
        )
    }

    private fun getFetchFeedByPopularityUseCase(): FetchFeedByPopularityUseCase {
        return FetchFeedByPopularityUseCase(FirebaseFirestore.getInstance())
    }

    private fun getFetchFeedByDataUseCase(): FetchFeedByDateUseCase {
        return FetchFeedByDateUseCase(FirebaseFirestore.getInstance())
    }

    // --------- Answer Screen ------------

    fun getFetchAnswerUseCase(doubtId: String): FetchAnswerUseCase {
        return FetchAnswerUseCase(FirebaseFirestore.getInstance(), doubtId)
    }

    fun getPublishAnswerUseCase(): PublishAnswerUseCase {
        return PublishAnswerUseCase(getServer())
    }

    // --------- Search ------------

    fun getExtractKeywordsUseCase(): ExtractKeywordsUseCase {
        return ExtractKeywordsUseCase()
    }

    fun getFetchSearchResultsUseCase(): FetchSearchResultsUseCase {
        return FetchSearchResultsUseCase(getServer())
    }

    // --------- OnBoarding ------------

    fun getFetchOnBoardingDataUseCase(user: User): FetchOnBoardingDataUseCase {
        return FetchOnBoardingDataUseCase(FirebaseFirestore.getInstance(), user)
    }

    fun getAddOnBoardingDataUseCase(user: User): AddOnBoardingDataUseCase {
        return AddOnBoardingDataUseCase(FirebaseFirestore.getInstance(), user)
    }


    // ------- Create Doubt ---------

    fun getDoubtDataSharedPrefUseCase(): DoubtDataSharedPrefUseCase {
        return DoubtDataSharedPrefUseCase(getSharedPref())
    }

    fun getPostDoubtUseCase(): PostDoubtUseCase {
        return PostDoubtUseCase(getServer())
    }

    // --------- InApp Notification ----------

    fun getFetchNotificationUseCase(): FetchInAppNotificationUseCase {
        return FetchInAppNotificationUseCase(getInAppNotificationDao(), getFetchUnreadNotificationUseCase())
    }

    fun getMarkInAppNotificationsReadUseCase(): MarkInAppNotificationsReadUseCase {
        return MarkInAppNotificationsReadUseCase(getInAppNotificationDao(), FirebaseFirestore.getInstance())
    }

    private fun getFetchUnreadNotificationUseCase(): FetchUnreadNotificationUseCase {
        return FetchUnreadNotificationUseCase(FirebaseFirestore.getInstance(), getUserManager())
    }

    private fun getInAppNotificationDao(): InAppNotificationDao {
        return AppDatabase.getDbInstance().inAppNotificationDao()
    }

    // ------- Common --------

    fun getAnswerVotingDoubtCase(answerData: AnswerData): VotingUseCase {
        return VotingUseCase(
            FirebaseFirestore.getInstance(),
            getUserManager().getCachedUserData()!!,
            true,
            answerData,
            null
        )
    }

    fun getDoubtVotingDoubtCase(doubtData: DoubtData): VotingUseCase {
        return VotingUseCase(
            FirebaseFirestore.getInstance(),
            getUserManager().getCachedUserData()!!,
            false,
            null,
            doubtData
        )
    }

    fun getFetchDoubtDataFromDoubtIdUseCase(): FetchDoubtDataFromDoubtIdUseCase {
        return FetchDoubtDataFromDoubtIdUseCase(FirebaseFirestore.getInstance())
    }

    // ------- User ---------

    private lateinit var userManager: UserManager

    @Synchronized
    fun getUserManager(): UserManager {

        if (::userManager.isInitialized == false) {
            userManager =
                UserManager(FirebaseAuth.getInstance(), getUserDataServer(), getUserDataStorage())
        }

        return userManager
    }

    private lateinit var userDataStorageUseCase: UserDataStorageUseCase

    @Synchronized
    private fun getUserDataStorage(): UserDataStorageUseCase {

        if (::userDataStorageUseCase.isInitialized == false) {
            userDataStorageUseCase = UserDataStorageUseCase(getSharedPref(), getGson())
        }

        return userDataStorageUseCase
    }

    private lateinit var userDataServerUseCase: UserDataServerUseCase

    @Synchronized
    private fun getUserDataServer(): UserDataServerUseCase {

        if (::userDataServerUseCase.isInitialized == false) {
            userDataServerUseCase =
                UserDataServerUseCase(FirebaseFirestore.getInstance(), getGson())
        }

        return userDataServerUseCase
    }

    // ---------- Navigation ----------

    val router = Router()

    fun getHomeFragNavigator(mainActivity: MainActivity): FragNavigator? {

        val homeFrag =
            (mainActivity.supportFragmentManager.findFragmentByTag("MainFragment") as MainFragment?)
                ?.childFragmentManager?.findFragmentByTag("mainfrag_0")

        if (homeFrag != null) {
            return DoubtlessApp.getInstance().getAppCompRoot()
                .getFragNavigator(homeFrag.childFragmentManager, R.id.bottomNav_child_container)
        }

        return null
    }

    fun getCreateFragmentNavigator(mainActivity: MainActivity): FragNavigator? {
        val createFrag =
            (mainActivity.supportFragmentManager.findFragmentByTag("MainFragment") as MainFragment?)
                ?.childFragmentManager?.findFragmentByTag("mainfrag_1")

        if (createFrag != null) {
            return DoubtlessApp.getInstance().getAppCompRoot()
                .getFragNavigator(createFrag.childFragmentManager, R.id.bottomNav_child_container)
        }

        return null
    }

    fun getInAppFragNavigator(mainActivity: MainActivity): FragNavigator? {

        val inAppFrag =
            (mainActivity.supportFragmentManager.findFragmentByTag("MainFragment") as MainFragment?)
                ?.childFragmentManager?.findFragmentByTag("mainfrag_2")

        if (inAppFrag != null) {
            return DoubtlessApp.getInstance().getAppCompRoot()
                .getFragNavigator(inAppFrag.childFragmentManager, R.id.bottomNav_child_container)
        }

        return null
    }

    fun getDashboardFragNavigator(mainActivity: MainActivity): FragNavigator? {

        val dashboardFrag =
            (mainActivity.supportFragmentManager.findFragmentByTag("MainFragment") as MainFragment?)
                ?.childFragmentManager?.findFragmentByTag("mainfrag_3")

        if (dashboardFrag != null) {
            return DoubtlessApp.getInstance().getAppCompRoot()
                .getFragNavigator(
                    dashboardFrag.childFragmentManager,
                    R.id.bottomNav_child_container
                )
        }

        return null
    }

    private fun getFragNavigator(
        supportFragmentManager: FragmentManager,
        @IdRes containerId: Int
    ): FragNavigator {
        return FragNavigator(
            containerId,
            supportFragmentManager
        )
    }

    // -------- Shared Pref ---------

    private fun getSharedPref(): SharedPreferences {
        return DoubtlessApp.getInstance()
            .getSharedPreferences("doubtless_shared_pref", Context.MODE_PRIVATE)
    }

    // --------- SERVER -----------

    private val BASE_URL = "https://asia-south1-doubtless-bd798.cloudfunctions.net/doubtless/api/"
    private lateinit var doubtlessServer: DoubtlessServer

    @Synchronized
    fun getServer(): DoubtlessServer {

        if (::doubtlessServer.isInitialized == false) {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

            doubtlessServer = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    /* factory = */ GsonConverterFactory.create(getGson())
                )
                .build()
                .create(DoubtlessServer::class.java)
        }

        return doubtlessServer
    }

    // --------- GSON -----------

    private fun getGson(): Gson {
        return Gson()
    }

    // --------- Remote Config -----------

    fun getRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10 * 60).build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        return remoteConfig
    }

    fun getFetchUserDataUseCase(): FetchUserDataUseCase {
        return FetchUserDataUseCase(
            FetchUserFeedByDateUseCase(FirebaseFirestore.getInstance()),
            FirebaseFirestore.getInstance()
        )
    }

    fun getDeleteAccountUseCase(): DeleteAccountUseCase {
        return DeleteAccountUseCase(
            FirebaseFirestore.getInstance()
        )
    }

    fun getFetchFilterTagsUseCase(): FetchFilterTagsUseCase {
        return FetchFilterTagsUseCase(
            FirebaseFirestore.getInstance()
        )
    }
}