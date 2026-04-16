# ── Stack traces legibles en crash reports ────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Room ───────────────────────────────────────────────────────────────────────
# Room genera código en tiempo de compilación pero accede a entidades y DAOs
# mediante reflexión en algunos casos.
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# ── Modelos de dominio ─────────────────────────────────────────────────────────
# Evita que R8 ofusque los data classes del modelo, que Room y DataStore
# leen por nombre de campo.
-keep class com.alejandroestevemaza.gymtimerpro.core.model.** { *; }

# ── Enums ──────────────────────────────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
