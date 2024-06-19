package com.example.catapult.users

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.catapult.cats.network.serialization.JsonAndClass
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class UsersDataSerializer() : Serializer<UsersData> {

    private val json: Json = JsonAndClass

    override val defaultValue: UsersData = UsersData.EMPTY

    override suspend fun readFrom(input: InputStream): UsersData {
        val text = String(input.readBytes(), charset = StandardCharsets.UTF_8)
        return try {
            json.decodeFromString<UsersData>(text)
        } catch (error: SerializationException) {
            throw CorruptionException(message = "Unable to deserialize file.", cause = error)
        } catch (error: IllegalArgumentException) {
            throw CorruptionException(message = "Unable to deserialize file.", cause = error)
        }
    }

    override suspend fun writeTo(t: UsersData, output: OutputStream) {
        val text = json.encodeToString(t)
        output.write(text.toByteArray())
    }
}