package com.example.catapult.users

import androidx.datastore.core.DataStore
import com.example.catapult.di.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.toImmutableList
import javax.inject.Inject

class UsersDataStore @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val dataStore: DataStore<UsersData>
) {

    private val scope = CoroutineScope(dispatcherProvider.io())

    val data = dataStore.data.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = runBlocking { dataStore.data.first() }
    )


    suspend fun updateList(users: List<User>, pick: Int): UsersData {
        return dataStore.updateData {
            it.copy(users = users, pick = pick)
        }
    }

    suspend fun changeMainUser(newPick: Int) : UsersData {
        return dataStore.updateData {
            it.copy(pick = newPick)
        }
    }

    suspend fun addUser(user: User) : UsersData {
        val users = data.value.users.toMutableList()
        users.add(user)

        return updateList(users = users.toImmutableList(), pick = users.size - 1)
    }

    suspend fun removeUser(user: User) : UsersData {
        val users = data.value.users.toMutableList()
        users.remove(user)

        return updateList(users = users.toImmutableList(), pick = users.size - 1)
    }

    suspend fun removeAllUsers(): UsersData {
        return updateList(users = emptyList(), pick = -1)
    }

}