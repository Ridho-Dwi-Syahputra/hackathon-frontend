package com.sako

import android.app.Application
import android.util.Log

class SakoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize application-wide configurations
        Log.d("SakoApplication", "Application started")

        // TODO: Initialize Timber for logging (optional)
        // if (BuildConfig.DEBUG) {
        //     Timber.plant(Timber.DebugTree())
        // }

        // TODO: Initialize Firebase (optional)
        // FirebaseApp.initializeApp(this)

        // TODO: Initialize Crashlytics (optional)
        // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}