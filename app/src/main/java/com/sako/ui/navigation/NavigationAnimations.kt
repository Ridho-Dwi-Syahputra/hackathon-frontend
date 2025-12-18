package com.sako.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

/**
 * Lightweight navigation animations optimized for performance
 * Using shorter durations and simpler transitions to reduce jank
 */
object NavigationAnimations {
    
    // Shorter duration for faster, smoother animations
    private const val ANIMATION_DURATION = 200  // Reduced from default 300ms
    
    /**
     * Fast fade transition - most performant
     * Use for screens with complex layouts
     */
    fun fadeEnter(): EnterTransition {
        return fadeIn(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    
    fun fadeExit(): ExitTransition {
        return fadeOut(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    
    /**
     * Lightweight slide + fade - balanced performance
     * Use for most navigation
     */
    fun slideInFade(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { it / 4 },  // Reduced distance for smoother animation
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    
    fun slideOutFade(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { -it / 4 },  // Reduced distance
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    
    fun slideInPopFade(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { -it / 4 },
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    
    fun slideOutPopFade(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { it / 4 },
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
    
    /**
     * No animation - instant transition
     * Use for very complex screens or when animation causes issues
     */
    fun noAnimation(): EnterTransition = EnterTransition.None
    
    fun noAnimationExit(): ExitTransition = ExitTransition.None
}
