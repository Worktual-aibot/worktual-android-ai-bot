package com.worktual.aibot.internal

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.worktual.aibot.WorktualAIBotConfig

internal class LoadingOverlayView(
    context: Context,
    private val config: WorktualAIBotConfig
) : FrameLayout(context) {

    private val progressFill: View
    private val progressTrack: View
    private var progressAnimatorSet: AnimatorSet? = null
    private var pulseAnimator: ObjectAnimator? = null

    init {
        setBackgroundColor(config.loadingBackground)
        isClickable = true // block touches from reaching WebView behind

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        // Logo or Spinner
        if (config.loadingLogoResId != null) {
            val logo = ImageView(context).apply {
                setImageResource(config.loadingLogoResId!!)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            val logoParams = LinearLayout.LayoutParams(
                dpToPx(config.loadingLogoWidth),
                dpToPx(config.loadingLogoHeight)
            ).apply { bottomMargin = dpToPx(20) }
            container.addView(logo, logoParams)
        } else {
            val spinner = ProgressBar(context).apply {
                isIndeterminate = true
                indeterminateTintList =
                    android.content.res.ColorStateList.valueOf(config.primaryColor)
            }
            val spinnerParams = LinearLayout.LayoutParams(
                dpToPx(48), dpToPx(48)
            ).apply { bottomMargin = dpToPx(20) }
            container.addView(spinner, spinnerParams)
        }

        // Title
        val title = TextView(context).apply {
            text = config.loadingTitle
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#1a1a2e"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        val titleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dpToPx(6) }
        container.addView(title, titleParams)

        // Subtitle
        val subtitle = TextView(context).apply {
            text = config.loadingSubtitle
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#8e8ea0"))
            gravity = Gravity.CENTER
        }
        val subtitleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dpToPx(28) }
        container.addView(subtitle, subtitleParams)

        // Pulse animation on subtitle
        pulseAnimator = ObjectAnimator.ofFloat(subtitle, "alpha", 1f, 0.3f).apply {
            duration = 500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }

        // Progress track
        val trackBg = GradientDrawable().apply {
            setColor(Color.parseColor("#e4e4e7"))
            cornerRadius = dpToPx(2).toFloat()
        }
        progressTrack = View(context).apply { background = trackBg }
        val trackFrame = FrameLayout(context)
        val trackParams = LinearLayout.LayoutParams(dpToPx(200), dpToPx(4))
        trackFrame.addView(progressTrack, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // Progress fill
        val fillBg = GradientDrawable().apply {
            setColor(config.primaryColor)
            cornerRadius = dpToPx(2).toFloat()
        }
        progressFill = View(context).apply { background = fillBg }
        trackFrame.addView(
            progressFill,
            LayoutParams(0, LayoutParams.MATCH_PARENT)
        )

        container.addView(trackFrame, trackParams)

        // Center the container
        addView(container, LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        ))
    }

    fun startAnimations() {
        pulseAnimator?.start()

        val trackWidth = dpToPx(200)

        // Fast to 70% in 800ms, then slow to 90% in 2500ms
        val anim1 = ValueAnimator.ofInt(0, (trackWidth * 0.7f).toInt()).apply {
            duration = 800
            interpolator = LinearInterpolator()
            addUpdateListener {
                progressFill.layoutParams = progressFill.layoutParams.apply {
                    width = it.animatedValue as Int
                }
            }
        }

        val anim2 = ValueAnimator.ofInt(
            (trackWidth * 0.7f).toInt(),
            (trackWidth * 0.9f).toInt()
        ).apply {
            duration = 2500
            interpolator = LinearInterpolator()
            addUpdateListener {
                progressFill.layoutParams = progressFill.layoutParams.apply {
                    width = it.animatedValue as Int
                }
            }
        }

        progressAnimatorSet = AnimatorSet().apply {
            playSequentially(anim1, anim2)
            start()
        }
    }

    fun completeAndHide(onHidden: () -> Unit) {
        progressAnimatorSet?.cancel()
        pulseAnimator?.cancel()

        val trackWidth = dpToPx(200)

        // Complete progress to 100% in 150ms
        val complete = ValueAnimator.ofInt(
            progressFill.layoutParams.width,
            trackWidth
        ).apply {
            duration = 150
            addUpdateListener {
                progressFill.layoutParams = progressFill.layoutParams.apply {
                    width = it.animatedValue as Int
                }
            }
        }

        complete.start()

        // Fade out after 150ms
        postDelayed({
            animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    visibility = GONE
                    onHidden()
                }
                .start()
        }, 150)
    }

    fun cleanup() {
        progressAnimatorSet?.cancel()
        pulseAnimator?.cancel()
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}
