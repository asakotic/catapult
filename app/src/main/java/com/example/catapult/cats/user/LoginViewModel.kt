package com.example.catapult.cats.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.users.User
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val usersData: UsersDataStore
) : ViewModel() {

    private val _loginState = MutableStateFlow(ILoginContract.LoginState())
    val loginState = _loginState.asStateFlow()

    private val _loginEvents = MutableSharedFlow<ILoginContract.LoginUIEvent>()

    private fun setLoginState(updateWith: ILoginContract.LoginState.() -> ILoginContract.LoginState) =
        _loginState.getAndUpdate(updateWith)

    fun setLoginEvent(event: ILoginContract.LoginUIEvent) = viewModelScope.launch {  _loginEvents.emit(event) }

    init {
        observerEvents()
    }



    private fun observerEvents() {
        viewModelScope.launch {
            _loginEvents.collect {
                when (it) {
                    is ILoginContract.LoginUIEvent.EmailInputChanged -> emailChange(it.email)
                    is ILoginContract.LoginUIEvent.NameInputChanged -> nameChange(it.name)
                    is ILoginContract.LoginUIEvent.NicknameInputChanged -> nicknameChange(it.nickname)
                }
            }
        }
    }


    private fun emailChange(email: String) {
        viewModelScope.launch {
            setLoginState { copy(email = email) }
        }
    }

    private fun nameChange(name: String) {
        viewModelScope.launch {
            setLoginState { copy(name = name) }
        }
    }

    private fun nicknameChange(nickname: String) {
        viewModelScope.launch {
            setLoginState { copy(nickname = nickname) }
        }
    }
}
