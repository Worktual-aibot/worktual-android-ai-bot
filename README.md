# Worktual AI Bot — Android SDK

Drop-in AI chatbot for native Android apps. One line to launch.

## Installation

Add JitPack to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency in your app `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.user:worktual-android-ai-bot:1.0.0")
}
```

## Usage

### Simple — Launch as Activity (one line)

```kotlin
WorktualAIBotActivity.launch(this, "YOUR_WEBCHAT_ID")
```

The bot opens full-screen and closes itself when the user is done.

### Advanced — Embed as View

```kotlin
val bot = WorktualAIBot(
    context = this,
    config = WorktualAIBotConfig(webchatId = "YOUR_WEBCHAT_ID"),
    listener = object : WorktualAIBotListener {
        override fun onClose() {
            finish()
        }
    }
)

// Add to any ViewGroup or set as content
setContentView(bot)
```

### Instant Loading (Preload)

Preload the bot in your `Application` or main Activity so it opens instantly:

```kotlin
// Early in app lifecycle
val preloader = WorktualAIBotPreloader(this, "YOUR_WEBCHAT_ID")
preloader.preload()

// Later — bot opens from cache, near instant
WorktualAIBotActivity.launch(this, "YOUR_WEBCHAT_ID")
```

## Configuration

```kotlin
val config = WorktualAIBotConfig(
    webchatId = "YOUR_WEBCHAT_ID",
    loadingLogoResId = R.drawable.my_logo,
    loadingTitle = "Support Chat",
    primaryColor = Color.parseColor("#FF6B00"),
    loadingBackground = Color.parseColor("#FFF8F0")
)
```

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `webchatId` | `String` | Required | Your webchat ID from Worktual |
| `baseUrl` | `String` | Production URL | Custom URL if self-hosted |
| `loadingLogoResId` | `Int?` | `null` (spinner) | Drawable resource for logo |
| `loadingTitle` | `String` | `"AI Assistant"` | Loading screen title |
| `loadingSubtitle` | `String` | `"Loading your chat..."` | Loading screen subtitle |
| `primaryColor` | `Int` | `#575CFF` | Progress bar colour |
| `loadingBackground` | `Int` | `#F8F9FB` | Loading screen background |
| `maxLoadTimeMs` | `Long` | `6000` | Max wait before force-showing chat |

## Requirements

- Android API 24+ (Android 7.0)
- AndroidX

## Support

Contact your Worktual account manager for your `webchatId`.
