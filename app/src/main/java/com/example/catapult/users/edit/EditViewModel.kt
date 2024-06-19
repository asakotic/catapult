package com.example.catapult.users.edit

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.core.getPic
import com.example.catapult.core.isDefaultImage
import com.example.catapult.di.DispatcherProvider
import com.example.catapult.users.UsersDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.io.File
import java.io.FileOutputStream
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val usersData: UsersDataStore
) : ViewModel() {

    private val _editState = MutableStateFlow(IEditContract.EditState(
        name = usersData.data.value.users[usersData.data.value.pick].name,
        nickname = usersData.data.value.users[usersData.data.value.pick].nickname,
        email = usersData.data.value.users[usersData.data.value.pick].email,
        image = usersData.data.value.users[usersData.data.value.pick].image
    ))
    val editState = _editState.asStateFlow()

    private val _editEvents = MutableSharedFlow<IEditContract.EditUIEvent>()

    private fun setEditState(updateWith: IEditContract.EditState.() -> IEditContract.EditState) =
        _editState.getAndUpdate(updateWith)

    fun setEditEvent(event: IEditContract.EditUIEvent) =
        viewModelScope.launch { _editEvents.emit(event) }

    init {
        if (!isDefaultImage(editState.value.image)) {
            viewModelScope.launch {
                setEditState { copy(bitmap = getPic(editState.value.image)) }
            }
        }
        observerEvents()
    }


    private fun observerEvents() {
        viewModelScope.launch {
            _editEvents.collect {
                when (it) {
                    is IEditContract.EditUIEvent.EmailInputChanged -> emailChange(it.email)
                    is IEditContract.EditUIEvent.NameInputChanged -> nameChange(it.name)
                    is IEditContract.EditUIEvent.NicknameInputChanged -> nicknameChange(it.nickname)
                    is IEditContract.EditUIEvent.ImageChanged -> savePic(imageView = it.imageView, subfolder = it.subfolder, pictureName =  it.pictureName)
                    is IEditContract.EditUIEvent.SaveChanges -> updateUser()
                }
            }
        }
    }


    fun isInfoValid(): Boolean {
        if (editState.value.name.isEmpty())
            return false
        if (editState.value.nickname.isEmpty())
            return false
        if (editState.value.email.isEmpty())
            return false
        return true
    }

    private fun updateUser() {
        val users = usersData.data.value.users.toMutableList()
        val pick = usersData.data.value.pick

        users[pick] = users[pick].copy(
            name = editState.value.name,
            nickname = editState.value.nickname,
            email = editState.value.email,
            image = editState.value.image
        )

        viewModelScope.launch {
            usersData.updateUser(users.toImmutableList())
            setEditState { copy(saveUserPassed = true) }
        }
    }

    private fun savePic(imageView: ImageView, subfolder: String, pictureName: String) {
        val drawable = imageView.getDrawable() as BitmapDrawable
        val bitmap = drawable.bitmap
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString(),
                "$pictureName.jpg"
            )
            if (!file.exists()) {
                Log.d("FILE", "Folder '$file' doesn't exist, creating it...")
                val rv = Objects.requireNonNull(file.getParentFile()).mkdir()
                Log.d("FILE", "Folder creation " + if (rv) "success" else "failed")
            } else {
                Log.d("FILE", "Folder already exists.")
            }
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            viewModelScope.launch {
                setEditState { copy(image = file.absolutePath, bitmap = getPic(file.absolutePath)) }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun emailChange(email: String) {
        viewModelScope.launch {
            setEditState { copy(email = email) }
        }
    }

    private fun nameChange(name: String) {
        viewModelScope.launch {
            setEditState { copy(name = name) }
        }
    }

    private fun nicknameChange(nickname: String) {
        viewModelScope.launch {
            setEditState { copy(nickname = nickname) }
        }
    }
}