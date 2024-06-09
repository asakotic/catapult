package com.example.catapult.cats.quiz.left_right_cat

import androidx.lifecycle.ViewModel
import com.example.catapult.di.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeftRightCatViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,

) : ViewModel() {
}