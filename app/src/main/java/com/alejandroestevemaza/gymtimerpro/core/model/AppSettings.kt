package com.alejandroestevemaza.gymtimerpro.core.model

enum class WeightUnitPreference(val storageValue: Int) {
    Automatic(0),
    Kilograms(1),
    Pounds(2);

    companion object {
        fun fromStorageValue(value: Int): WeightUnitPreference =
            entries.firstOrNull { it.storageValue == value } ?: Automatic
    }
}

enum class TimerDisplayFormat(val storageValue: Int) {
    Seconds(0),
    MinutesAndSeconds(1);

    companion object {
        fun fromStorageValue(value: Int): TimerDisplayFormat =
            entries.firstOrNull { it.storageValue == value } ?: Seconds
    }
}

enum class MaxSetsPreference(val maxSets: Int) {
    Ten(10),
    Fifteen(15),
    Twenty(20),
    Thirty(30);

    companion object {
        fun fromStorageValue(value: Int): MaxSetsPreference =
            entries.firstOrNull { it.maxSets == value } ?: Ten
    }
}

enum class RestIncrementPreference(val seconds: Int) {
    Five(5),
    Ten(10),
    Fifteen(15);

    companion object {
        fun fromStorageValue(value: Int): RestIncrementPreference =
            entries.firstOrNull { it.seconds == value } ?: Fifteen
    }
}

enum class EnergySavingMode(val storageValue: Int) {
    Off(0),
    Automatic(1),
    On(2);

    companion object {
        fun fromStorageValue(value: Int): EnergySavingMode =
            entries.firstOrNull { it.storageValue == value } ?: Off
    }
}

data class AppSettings(
    val weightUnitPreference: WeightUnitPreference = WeightUnitPreference.Automatic,
    val timerDisplayFormat: TimerDisplayFormat = TimerDisplayFormat.Seconds,
    val maxSetsPreference: MaxSetsPreference = MaxSetsPreference.Ten,
    val restIncrementPreference: RestIncrementPreference = RestIncrementPreference.Fifteen,
    val energySavingMode: EnergySavingMode = EnergySavingMode.Off,
)
