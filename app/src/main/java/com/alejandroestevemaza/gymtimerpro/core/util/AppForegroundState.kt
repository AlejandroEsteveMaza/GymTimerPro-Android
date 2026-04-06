package com.alejandroestevemaza.gymtimerpro.core.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.concurrent.atomic.AtomicBoolean

object AppForegroundState : DefaultLifecycleObserver {
    private val isForeground = AtomicBoolean(false)
    @Volatile private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun isForeground(): Boolean = isForeground.get()

    override fun onStart(owner: LifecycleOwner) {
        isForeground.set(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        isForeground.set(false)
    }
}
