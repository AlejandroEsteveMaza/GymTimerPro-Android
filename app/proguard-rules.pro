# ── Stack traces legibles ──────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Kotlin ─────────────────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# ── Jetpack Compose ────────────────────────────────────────────────────────────
# Las reglas de Compose vienen incluidas en la librería. No se necesitan reglas
# adicionales salvo para las previews (solo en debug).
-keep class androidx.compose.runtime.** { *; }

# ── Room ───────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.**

# ── DataStore ──────────────────────────────────────────────────────────────────
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# ── Google Play Billing ────────────────────────────────────────────────────────
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# ── Modelos de dominio (no ofuscar para evitar problemas con serialización) ────
-keep class com.alejandroestevemaza.gymtimerpro.core.model.** { *; }

# ── Enums ──────────────────────────────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ── Parcelable ─────────────────────────────────────────────────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ── Serializable ───────────────────────────────────────────────────────────────
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ── OkHttp / Okio (dependencia transitiva) ────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**

# ── Suprimir advertencias de librerías de terceros ────────────────────────────
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
