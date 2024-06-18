package com.example.catapult.cats.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.navigation.addNewUser
import com.example.catapult.users.User
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val usersData: UsersDataStore
) : ViewModel() {

    private val addNewUser = savedStateHandle.addNewUser
    private val _loginState = MutableStateFlow(ILoginContract.LoginState(addNewUser = addNewUser))
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
                    is ILoginContract.LoginUIEvent.AddUser -> addUser()
                }
            }
        }
    }

    fun isInfoValid(): Boolean {
        if (loginState.value.name.isEmpty())
            return false
        if (loginState.value.nickname.isEmpty())
            return false
        if (loginState.value.email.isEmpty())
            return false
        return true
    }

    private fun addUser() {
        val user = User(name = loginState.value.name, nickname = loginState.value.nickname, email = loginState.value.email)

        //waits for user to be added, then goes to new page
        runBlocking {
            usersData.addUser(user)
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

    fun hasAccount(): Boolean {
        return usersData.data.value.pick != -1 && !loginState.value.addNewUser
    }
}
