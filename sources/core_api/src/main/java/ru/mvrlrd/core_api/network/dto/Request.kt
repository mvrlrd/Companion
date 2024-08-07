package ru.mvrlrd.core_api.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Named


sealed interface Request{

}

@Serializable
@SerialName("CompletionOptions")
data class CompletionOptionsDto(
    @SerialName("stream")
    val stream: Boolean,
    @SerialName("temperature")
    val temperature: Double,
    @SerialName("maxTokens")
    val maxTokens: String
) {
    companion object {
        fun getDefault() = CompletionOptionsDto(
            false,
            0.6,
            "2000"
        )
    }
}

@Serializable
@SerialName("Message")
data class MessageDto(
    @SerialName("role")
    val role: String,
    @SerialName("text")
    val text: String
)

@Serializable
@SerialName("RequestData")
data class RequestDataDto(
    @SerialName("modelUri")
    val modelUri: String="",
    @SerialName("completionOptions")
    val completionOptionsDto: CompletionOptionsDto,
    @SerialName("messages")
    val messageDtos: List<MessageDto>
) : Request {
    companion object {
        fun getDefault(@Named("modelUrl") modelUri: String ="ассистент", listOfMessageDtos: List<MessageDto>): RequestDataDto =
            RequestDataDto(
                modelUri = modelUri,
                completionOptionsDto = CompletionOptionsDto.getDefault(),
                messageDtos = listOfMessageDtos
            )
    }
}
data object BadRequest: Request