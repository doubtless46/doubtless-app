package com.doubtless.doubtless

import android.app.Application
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.doubtless.doubtless.di.AppCompositionRoot

class DoubtlessApp : Application() {

    private lateinit var appCompositionRoot: AppCompositionRoot

    override fun onCreate() {
        super.onCreate()
        instance = this
        appCompositionRoot = AppCompositionRoot(instance)
    }

    companion object {
        private lateinit var instance: DoubtlessApp

        @JvmStatic
        fun getInstance(): DoubtlessApp {
            return instance
        }
    }

    fun getAppCompRoot(): AppCompositionRoot {
        return appCompositionRoot
    }

}