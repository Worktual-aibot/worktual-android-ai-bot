# Keep JavascriptInterface methods for WebView bridge
-keepclassmembers class com.worktual.aibot.internal.** {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class com.worktual.aibot.WorktualAIBot$* {
    @android.webkit.JavascriptInterface <methods>;
}
