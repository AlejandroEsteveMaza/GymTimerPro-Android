package com.alejandroestevemaza.gymtimerpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.alejandroestevemaza.gymtimerpro.app.GymTimerProApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GymTimerProApp(
                appContainer = (application as GymTimerProApplication).appContainer
            )
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            (application as GymTimerProApplication).appContainer.premiumStateRepository.refresh()
        }
    }
}
