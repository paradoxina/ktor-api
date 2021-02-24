import kotlinx.serialization.Serializable

@Serializable sealed class RequestData {
    @Serializable data class ActA(val id: Int =1, val action: String= "das", val status: Boolean, val number: Int = 0) : RequestData()
    @Serializable data class ActB(val id: Int =2, val action: String, val status: Boolean, val number: Int = 0) : RequestData()
    @Serializable data class Error(val message: String) : RequestData()
}