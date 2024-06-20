package com.example.catapult.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import java.io.File


fun seeResults(time: Int, points: Int): Float {
    //UBP = BTO * 2.5 * (1 + (PVT + 120) / MVT)
    var ubp: Float = points * 2.5F * (1 + (time + 120F) / 300)
    if (ubp > 100) ubp = 100f
    return ubp
}

fun getTimeAsFormat(timer: Int): String {
    val min = timer / 60
    val sec = if (timer % 60 < 10) "0${timer % 60}" else timer % 60
    return "${min}:${sec}"
}

fun defaultImage(): String {
    return "https://cdn2.thecatapi.com/images/J2PmlIizw.jpg"
}

fun isDefaultImage(imagePath: String): Boolean {
    return imagePath == defaultImage()
}

fun getPic(path: String): Bitmap {
    val file = File(path)
    return BitmapFactory.decodeFile(file.absolutePath)
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }