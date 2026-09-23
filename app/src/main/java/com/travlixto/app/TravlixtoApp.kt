package com.travlixto.app

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Application entry point. Initializes Firebase once for the whole app.
 * Make sure app/google-services.json (downloaded from your Firebase console)
 * is in place before building, or FirebaseApp.initializeApp will fail.
 */
class TravlixtoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
