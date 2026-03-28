# Worktual AI Bot — Android SDK

Drop-in AI chatbot for native Android apps. Preloads in background, opens instantly — no loading screen.

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

## Setup (2 Steps)

### Step 1 — Preload in MainActivity (runs once on app start)

```kotlin
import com.worktual.aibot.WorktualAIBotManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bot loads silently in background — user sees nothing
        WorktualAIBotManager.preload(this, "YOUR_WEBCHAT_ID")
    }

    override fun onDestroy() {
        WorktualAIBotManager.destroy()
        super.onDestroy()
    }
}
```

### Step 2 — Show/Hide on button tap

```kotlin
// Opens INSTANTLY — no loading screen!
chatButton.setOnClickListener {
    WorktualAIBotManager.show()
}
```

That's it. The bot closes itself automatically when the user taps the close button inside the chat.

## How It Works

1. `preload()` loads the bot WebView **hidden** in your Activity on app start
2. By the time the user taps the chat button, the bot is **already fully loaded**
3. `show()` just makes it visible — **instant, zero delay**
4. When user closes the chat, the bot hides but **stays loaded** in memory
5. Next `show()` is instant again — no re-downloading

## Handle Close Events (Optional)

```kotlin
WorktualAIBotManager.show(listener = object : WorktualAIBotListener {
    override fun onClose() {
        // Bot already hides automatically
        // Add any custom logic here
    }

    override fun onReady() {
        Log.d("Bot", "Bot is loaded and ready")
    }
})
```

## Custom Branding

```kotlin
import com.worktual.aibot.WorktualAIBotConfig
import com.worktual.aibot.WorktualAIBotManager

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
| `primaryColor` | `Int` | `#575CFF` | Progress bar colour |
| `loadingBackground` | `Int` | `#F8F9FB` | Background colour |

## Requirements

- Android API 24+ (Android 7.0)
- AndroidX

## Support

Contact your Worktual account manager for your `webchatId`.
