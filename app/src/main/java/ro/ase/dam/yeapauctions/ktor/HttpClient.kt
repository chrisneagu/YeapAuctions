import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import okhttp3.OkHttpClient
import org.bson.types.ObjectId
import ro.ase.dam.yeapauctions.classes.DateSerializer
import ro.ase.dam.yeapauctions.classes.ListObjectIdSerializer
import ro.ase.dam.yeapauctions.classes.ObjectIdSerializer
import java.util.*
import java.util.concurrent.TimeUnit


val KtorHttpClient = HttpClient(CIO) {
    expectSuccess = true

    defaultRequest {
        url {
            protocol = URLProtocol.HTTP
            host = "192.168.0.183"
            port = 5001
        }
        headers.appendIfNameAbsent(
            HttpHeaders.ContentType,
            ContentType.Application.Json.toString()
        )
    }

    Charsets {
        // Allow using `UTF_8`.
        register(Charsets.UTF_8)

        // Allow using `ISO_8859_1` with quality 0.1.
        register(Charsets.ISO_8859_1, quality=0.1f)
    }

    install(HttpTimeout)

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                encodeDefaults = true

                val serializersModule = SerializersModule {
                    contextual(ObjectId::class, ObjectIdSerializer)
                    contextual(Date::class, DateSerializer)
                }
            }
        )
    }

    install(UserAgent) {
        agent = "Ktor Client"
    }

    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 0)
        retryOnException(maxRetries = 0)
        exponentialDelay()
    }

    install(HttpCache)

    install(HttpRedirect) {
        checkHttpMethod = false
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.INFO
    }

    install(WebSockets){
        pingInterval = 20_000
    }

}
