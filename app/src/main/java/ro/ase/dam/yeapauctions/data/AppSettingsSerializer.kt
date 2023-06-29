package ro.ase.dam.yeapauctions.data

import androidx.datastore.core.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.io.InputStream
import java.io.OutputStream
import ro.ase.dam.yeapauctions.classes.ObjectIdSerializer

object AppSettingsSerializer : Serializer<AppSettings> {
    private val json = Json { serializersModule = SerializersModule { contextual(ObjectIdSerializer) } }

    override val defaultValue: AppSettings = AppSettings()

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            json.decodeFromStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.buffered().use {
            json.encodeToStream(t, it)
            it.flush()
        }
    }
}