package com.example.catapult.core

import android.util.Log

fun seeResults(time: Int, points:Int): Float{
    //UBP = BTO * 2.5 * (1 + (PVT + 120) / MVT)
    var ubp: Float = points * 2.5F * (1 + (time + 120F) / 300)
    if(ubp >100) ubp =100f
    return ubp
}

fun getTimeAsFormat(timer: Int): String {
    val min = timer/60
    val sec = if (timer%60 < 10) "0${timer%60}" else timer%60
    return "${min}:${sec}"
}