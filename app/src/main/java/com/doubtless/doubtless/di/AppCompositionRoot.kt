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
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.navigation.Router
import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.usecases.UserDataServerUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserDataStorageUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.usecases.DoubtDataSharedPrefUseCase
import com.doubtless.doubtless.screens.doubt.usecases.PostDoubtUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchFeedByDateUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchFeedByPopularityUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.onboarding.usecases.AddOnBoardingDataUseCase
import com.doubtless.doubtless.screens.onboarding.usecases.FetchOnBoardingDataUseCase
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.main.MainFragment
import com.doubtless.doubtless.screens.search.usecases.ExtractKeywordsUseCase
import com.doubtless.doubtless.screens.search.usecases.FetchSearchResultsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
    fun getFetchHomeFeedUseCase(): FetchHomeFeedUseCase {
        return FetchHomeFeedUseCase(
            getFetchFeedByDataUseCase(),
            getFetchFeedByPopularityUseCase(),
            FirebaseFirestore.getInstance()
        )
    }

    private fun getFetchFeedByPopularityUseCase(): FetchFeedByPopularityUseCase {
        return FetchFeedByPopularityUseCase(FirebaseFirestore.getInstance())
    }

    private fun getFetchFeedByDataUseCase(): FetchFeedByDateUseCase {
        return FetchFeedByDateUseCase(FirebaseFirestore.getInstance())
    }

    // --------- Search ------------

    fun getExtractKeywordsUseCase(): ExtractKeywordsUseCase {
        return ExtractKeywordsUseCase()
    }

    fun getFetchSearchResultsUseCase(): FetchSearchResultsUseCase {
        return FetchSearchResultsUseCase()
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

    // TODO : ig this should be home frag scoped.
    private lateinit var homeFragNavigator: FragNavigator

    fun getHomeFragNavigator(mainActivity: MainActivity): FragNavigator? {

        if (::homeFragNavigator.isInitialized) {
            return homeFragNavigator
        }

        val homeFrag =
            (mainActivity.supportFragmentManager.findFragmentByTag("MainFragment") as MainFragment?)
                ?.childFragmentManager?.findFragmentByTag("mainfrag_0")

        if (homeFrag != null)
            return DoubtlessApp.getInstance().getAppCompRoot()
                .getFragNavigator(homeFrag.childFragmentManager, R.id.home_container)

        return null
    }

    private fun getFragNavigator(supportFragmentManager: FragmentManager, @IdRes containerId: Int ): FragNavigator {
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

    private val BASE_URL = "https://TODO"
    private lateinit var doubtlessServer: DoubtlessServer

    @Synchronized
    fun getServer(): DoubtlessServer {

        if (::doubtlessServer.isInitialized == false) {
            doubtlessServer = Retrofit.Builder()
                .baseUrl(BASE_URL)
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
}