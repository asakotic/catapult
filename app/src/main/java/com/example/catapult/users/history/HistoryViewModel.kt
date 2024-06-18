package com.example.catapult.users.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val usersDataStore: UsersDataStore
) : ViewModel() {

    private val _historyState = MutableStateFlow(IHistoryContract.HistoryState(usersData = usersDataStore.data.value))
    val historyState = _historyState.asStateFlow()

    private val _historyEvent = MutableSharedFlow<IHistoryContract.HistoryUIEvent>()

    fun setHistoryEvent(event: IHistoryContract.HistoryUIEvent) = viewModelScope.launch { _historyEvent.emit(event) }
    private fun setHistoryState(updateWith: IHistoryContract.HistoryState.() -> IHistoryContract.HistoryState) =
        _historyState.getAndUpdate(updateWith)

    init {
        observeEvents()
    }

    fun getBestResult(quiz : String): String {
        val users = historyState.value.usersData.users
        val pick = historyState.value.usersData.pick

        return when (quiz) {
            "guessCat" -> users[pick].guessCat.bestResult.toString()
            "guessFact" -> users[pick].guessFact.bestResult.toString()
            "leftRightCat" -> users[pick].leftRightCat.bestResult.toString()
            else -> "0.0"
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _historyEvent.collect {
                when (it) {
                    is IHistoryContract.HistoryUIEvent.Expanded -> expandedChanged(it.index)
                }
            }
        }
    }

    private fun expandedChanged(index: Int) {
        val list = historyState.value.expandedList.toMutableList()
        list[index] = !list[index]
        viewModelScope.launch {
            setHistoryState { copy(expandedList = list.toImmutableList()) }
        }
    }
}