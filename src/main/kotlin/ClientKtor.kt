import RequestData.ActA
import RequestData.ActB
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.json.Json

val client = HttpClient {
    install(WebSockets)
}

suspend fun HttpClient.sendJson() {
    json<RequestData>(
        url = "http://127.0.0.1:8080/api",
        data = ActA(1, "A", false, 132),
    )
    protobuf<RequestData>(
        url = "http://127.0.0.1:8080/api",
        data = ActB(2, "B", false, 132),
    )
    cbor<RequestData>(
        url = "http://127.0.0.1:8080/api",
        data = ActB(2, "B", false),
    )
    ws(
        method = HttpMethod.Get,
        host = "127.0.0.1",
        port = 8080,
        path = "/ws"
    ) {
        val dataBytes = Json{encodeDefaults = false }.encodeToString(RequestData.serializer(), ActB(2, "B", false)).toByteArray(Charsets.UTF_8)
//        println(dataBytes.contentToString())
//        println(dataBytes.size)
        println(dataBytes.toString(Charsets.UTF_8))
        send(Frame.Binary(false, dataBytes))
        when (val frame = incoming.receive()) {
            //is Frame.Text -> println(frame.readText())
            is Frame.Binary -> println(frame.readBytes())
            else -> println("a")
        }
    }
}
