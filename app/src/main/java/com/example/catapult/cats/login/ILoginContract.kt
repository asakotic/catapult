package com.example.catapult.cats.login

interface ILoginContract {

    data class LoginState (
        val nickname: String = "",
        val name: String = "",
        val email: String = "",
        val addNewUser: Boolean,
        val loginCheckPassed: Boolean = false
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }

    sealed class LoginUIEvent{
        data class NicknameInputChanged(val nickname: String) : LoginUIEvent()
        data class EmailInputChanged(val email: String) : LoginUIEvent()
        data class NameInputChanged(val name: String) : LoginUIEvent()
        data object AddUser : LoginUIEvent()
    }
}