package com.example.catapult.core

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

class CustomRippleTheme(private val color: Color) : RippleTheme {
    @Composable
    override fun defaultColor() = color

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        pressedAlpha = 0.8f,
        focusedAlpha = 0.24f,
        draggedAlpha = 0.16f,
        hoveredAlpha = 0.08f,
    )

}