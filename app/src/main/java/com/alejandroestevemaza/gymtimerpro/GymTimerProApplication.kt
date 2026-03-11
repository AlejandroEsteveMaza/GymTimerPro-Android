package com.alejandroestevemaza.gymtimerpro

import android.app.Application
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer

class GymTimerProApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }
}
