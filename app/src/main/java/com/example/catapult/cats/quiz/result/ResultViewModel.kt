package com.example.catapult.cats.quiz.result

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.cats.db.CatsService
import com.example.catapult.cats.quiz.result.IResultContract.ResultState
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.navigation.category
import com.example.catapult.navigation.result
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatsService,
    private val usersDataStore: UsersDataStore
) : ViewModel() {
    private val categ: Int = savedStateHandle.category
    private val ress: Float = savedStateHandle.result
    private val _resultState = MutableStateFlow(ResultState())
    val resultState = _resultState.asStateFlow()
    private val _resultEvents = MutableSharedFlow<IResultContract.ResultUIEvent>()
    fun setEvent(event: IResultContract.ResultUIEvent) = viewModelScope.launch { _resultEvents.emit(event) }

    private fun setResultSate (update: ResultState.() -> ResultState) =
        _resultState.getAndUpdate(update)

    init {
        Log.d("test", usersDataStore.data.value.pick.toString())
        observeResult()
        observeEvents()
    }
    private fun observeEvents() {
        viewModelScope.launch {
            _resultEvents.collect { resultUIEvent ->
                when (resultUIEvent) {
                    is IResultContract.ResultUIEvent.PostResult -> post()
                }
            }
        }
    }

    private fun observeResult() {

        viewModelScope.launch {
            setResultSate { copy(isLoading = true) }
            try {
               setResultSate {
                   copy(
                       category = categ,
                       username =usersDataStore.data.value.users[usersDataStore.data.value.pick].nickname,
                       points = ress
                   )
               }
            }catch (error: IOException){
                setResultSate { copy(error = ResultState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setResultSate { copy( isLoading = false) }
            }

        }
    }
    private fun post(){
        viewModelScope.launch {
            setResultSate { copy(isLoading = true) }
            withContext(dispatcherProvider.io()) {
                val state = resultState.value
                catsService.postResult(state.username,state.points,state.category)
            }
            setResultSate { copy(isPosted = true, isLoading = false) }
        }
    }



}