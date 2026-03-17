package com.alejandroestevemaza.gymtimerpro.data.preferences

import android.content.Context
import androidx.room.Room
import com.alejandroestevemaza.gymtimerpro.BuildConfig
import com.alejandroestevemaza.gymtimerpro.data.local.AppDatabase
import com.alejandroestevemaza.gymtimerpro.data.repository.DefaultRoutinesRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.DefaultTrainingSessionCoordinator
import com.alejandroestevemaza.gymtimerpro.data.repository.DefaultWorkoutCompletionRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.RoutinesRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.TrainingSessionCoordinator
import com.alejandroestevemaza.gymtimerpro.data.repository.WorkoutCompletionRepository
import com.alejandroestevemaza.gymtimerpro.feature.training.notifications.AndroidRestNotificationCoordinator
import com.alejandroestevemaza.gymtimerpro.feature.training.notifications.RestNotificationCoordinator

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "gymtimerpro.db",
        ).build()
    }

    val appSettingsRepository: AppSettingsRepository by lazy {
        DataStoreAppSettingsRepository(appContext)
    }

    val premiumStateRepository: PremiumStateRepository by lazy {
        if (BuildConfig.DEBUG) {
            DebugPremiumStateRepository()
        } else {
            BillingPremiumStateRepository(appContext)
        }
    }

    val trainingSessionRepository: TrainingSessionRepository by lazy {
        DataStoreTrainingSessionRepository(appContext)
    }

    val restNotificationCoordinator: RestNotificationCoordinator by lazy {
        AndroidRestNotificationCoordinator(appContext)
    }

    val routinesRepository: RoutinesRepository by lazy {
        DefaultRoutinesRepository(database.routineDao())
    }

    val workoutCompletionRepository: WorkoutCompletionRepository by lazy {
        DefaultWorkoutCompletionRepository(database.workoutCompletionDao())
    }

    val trainingSessionCoordinator: TrainingSessionCoordinator by lazy {
        DefaultTrainingSessionCoordinator(trainingSessionRepository)
    }
}
