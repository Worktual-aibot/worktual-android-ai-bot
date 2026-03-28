# Worktual AI Bot — Android SDK

Drop-in AI chatbot for native Android apps. Preloads in background, opens instantly.

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
    implementation("com.github.Worktual-aibot:worktual-android-ai-bot:1.0.0")
}
```

## Usage (Recommended — Instant Open)

Preload the bot hidden in your main Activity. When the user taps your button, the bot opens **instantly** — no loading screen.

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Preload bot in background (hidden, loads WebView silently)
        WorktualAIBotManager.preload(this, "YOUR_WEBCHAT_ID")

        // When user taps button — opens instantly!
        findViewById<Button>(R.id.chatButton).setOnClickListener {
            WorktualAIBotManager.show(listener = object : WorktualAIBotListener {
                override fun onClose() {
                    // Bot hides automatically, stays loaded for next open
                }
            })
        }
    }

    override fun onDestroy() {
        WorktualAIBotManager.destroy()
        super.onDestroy()
    }
}
```

## Alternative — Launch as Activity (shows loading screen)

```kotlin
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

WorktualAIBotManager.preload(this, "", config = config)
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
