import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

object ServerKtor {
    fun start() =
        embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::routing).start(wait = false)
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId: AtomicInteger = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}

fun Application.routing() {
    install(WebSockets)
    install(ContentNegotiation) {
        json(Json.Default, ContentType.Application.Json)
        serialization(ContentType.Application.ProtoBuf, ProtoBuf.Default)
        serialization(ContentType.Application.Cbor, Cbor.Default)
    }
//    install(DefaultHeaders)
    install(CallLogging)
    install(StatusPages) {
        exception<BadRequestException> {
            call.respond(HttpStatusCode.BadRequest, RequestData.Error(it.message.orEmpty()))
        }
    }

    routing {
        post("/api") {
            val data = try {
                call.receive<RequestData>()
            } catch (e: SerializationException) {
                throw BadRequestException(e.message.orEmpty(), e)
            }
            call.respond(
                when (data) {
                    is RequestData.ActA -> data
                    is RequestData.ActB -> data
                    is RequestData.Error -> data
                }
            )
        }

        webSocket("/ws") {
            val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection

            println(incoming)
            for (frame in incoming) {
                when (frame) {
                    is Frame.Binary -> {
                        val str = frame.readBytes().toString(Charsets.UTF_8)
                        println(str)
                        val data =
                            Json { encodeDefaults = true;ignoreUnknownKeys=true }.decodeFromString<RequestData>(str)
//                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        val textWithUsername = "[${thisConnection.name}]: "
                        connections.forEach {
                            it.session.send(textWithUsername)
                            println(it.toString())
                        }
                        //println(data)

                    }
                    else -> println("b")
                }
            }
        }
    }
}
