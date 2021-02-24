import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

suspend fun ApplicationCall.respond(data: RequestData) = when (request.header(HttpHeaders.ContentType)) {
    ContentType.Application.ProtoBuf.toString() -> respond(object : OutgoingContent.ByteArrayContent() {
        override fun bytes() = ProtoBuf.encodeToByteArray(data)
    })
    ContentType.Application.Cbor.toString() -> respond(object : OutgoingContent.ByteArrayContent() {
        override fun bytes() = Cbor.encodeToByteArray(data)
    })
    else -> respond<RequestData>(data)
}

suspend inline fun <reified T : RequestData> HttpClient.cbor(
    url: String,
    data: T,
    builder: HttpRequestBuilder.() -> Unit = {}
) = Cbor.decodeFromByteArray<T>(post<ByteArray> {
    url(url)
    contentType(ContentType.Application.Cbor)
    body = Cbor.encodeToByteArray(RequestData.serializer(), data)
    builder()
}.log("client cbor"))

suspend inline fun <reified T : RequestData> HttpClient.json(
    url: String,
    data: RequestData,
    builder: HttpRequestBuilder.() -> Unit = {}
) = Json.decodeFromString<T>(post<String> {
    url(url)
    contentType(ContentType.Application.Json)
    body = Json.encodeToString(RequestData.serializer(), data)
    builder()
}.log("client json"))

suspend inline fun <reified T : RequestData> HttpClient.protobuf(
    url: String,
    data: RequestData,
    builder: HttpRequestBuilder.() -> Unit = {}
) = ProtoBuf.decodeFromByteArray<T>(post<ByteArray> {
    url(url)
    contentType(ContentType.Application.ProtoBuf)
    body = ProtoBuf.encodeToByteArray(RequestData.serializer(), data)
    builder()
}.log("client protobuf"))

inline fun <reified T : Any> T.log(text: String = "") = also {
    val str = when (this) {
        is ByteArray -> toString(Charsets.UTF_8)
        else -> toString()
    }
    println("$text: $str ${str.count()}")
}