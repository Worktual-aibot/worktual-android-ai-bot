package com.worktual.aibot.internal

import android.webkit.WebView
import android.webkit.WebViewClient

internal class BotWebViewClient(
    private val maxLoadTimeMs: Long
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(buildInjectedJS(), null)
    }

    private fun buildInjectedJS(): String = """
        (function() {
            // Polyfill: bot HTML calls ReactNativeWebView.postMessage()
            // Forward those messages to our native Android bridge
            window.ReactNativeWebView = {
                postMessage: function(msg) {
                    WorktualBridge.postMessage(msg);
                }
            };

            // Also intercept window.postMessage for iframe-based bots
            var origPostMessage = window.postMessage;
            window.postMessage = function(msg, origin) {
                try {
                    if (typeof msg === 'string') {
                        WorktualBridge.postMessage(msg);
                    } else {
                        WorktualBridge.postMessage(JSON.stringify(msg));
                    }
                } catch(e) {}
                return origPostMessage.call(window, msg, origin);
            };

            // Poll for chat content readiness
            var t = setInterval(function() {
                var m = document.querySelectorAll(
                    '.message, .chat-message, .msg-content, [class*="message"]'
                );
                var i = document.querySelector('input[placeholder], textarea[placeholder]');
                if (m.length > 0 || i) {
                    clearInterval(t);
                    WorktualBridge.postMessage(
                        JSON.stringify({ type: "webchat_ready" })
                    );
                }
            }, 200);

            // Timeout fallback
            setTimeout(function() {
                clearInterval(t);
                WorktualBridge.postMessage(
                    JSON.stringify({ type: "webchat_ready" })
                );
            }, $maxLoadTimeMs);
        })();
    """.trimIndent()
}
