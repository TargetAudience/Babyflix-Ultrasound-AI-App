package com.babyfilx.data.localdatabse

import androidx.datastore.core.Serializer
import com.babyfilx.data.models.LocalDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


object DatabaseSerializable : Serializer<LocalDataModel> {
    override val defaultValue: LocalDataModel
        get() = LocalDataModel()

    override suspend fun readFrom(input: InputStream): LocalDataModel {
        return try {
            Json.decodeFromString(
                deserializer = LocalDataModel.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: LocalDataModel, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = LocalDataModel.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}