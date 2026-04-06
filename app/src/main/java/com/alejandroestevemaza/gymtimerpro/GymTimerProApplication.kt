package com.alejandroestevemaza.gymtimerpro

import android.app.Application
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer
import com.alejandroestevemaza.gymtimerpro.core.util.AppForegroundState

class GymTimerProApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        AppForegroundState.init()
    }
}
