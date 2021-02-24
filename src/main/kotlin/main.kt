import io.ktor.client.features.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.Security

fun main() {
    // for windows websocket
    System.setProperty("io.ktor.random.secure.random.provider", "DRBG")
    Security.setProperty("securerandom.drbg.config", "HMAC_DRBG,SHA-512,256,pr_and_reseed")

    ServerKtor.start()
    GlobalScope.launch(Dispatchers.IO) {
        try {
            client.sendJson()
        } catch (e: Exception) {
            when (e) {
                is ClientRequestException -> {
                    println("client: bad request")
                }
                else -> {
                    e.printStackTrace()
                }
            }
        }
    }
}


