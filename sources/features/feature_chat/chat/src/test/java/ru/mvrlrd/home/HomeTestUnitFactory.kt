package ru.mvrlrd.home

import ru.mvrlrd.core_api.network.dto.AlternativeDto
import ru.mvrlrd.core_api.network.dto.MessageDto
import ru.mvrlrd.core_api.network.dto.ResultDto
import ru.mvrlrd.core_api.network.dto.ServerResponseDto
import ru.mvrlrd.core_api.network.dto.UsageDto
import ru.mvrlrd.feature_chat.data.MyResponseMapper
import ru.mvrlrd.feature_chat.domain.model.AIResponse

object HomeTestUnitFactory {
    fun getMapper() = MyResponseMapper()

    fun getAIResponse() = AIResponse(
        "ai response",
        "assistant",
        100,
        50,
        150,
        "1.0",
        1000L
    )

    private var serverResponseFactory: ServerResponseFactory = GoodResponseFactory()

    fun getBadServerResponse(): ServerResponseDto {
        serverResponseFactory = BadResponseFactory()
        return serverResponseFactory.createResponse()
    }

    fun getGoodServerResponse(): ServerResponseDto {
        serverResponseFactory = GoodResponseFactory()
        return serverResponseFactory.createResponse()
    }



}
abstract class ServerResponseFactory(){
    abstract fun createResponse(): ServerResponseDto
    abstract fun createResult(): ResultDto
    abstract fun createUsage():UsageDto
    abstract fun createAlternatives(): AlternativeDto
    abstract fun createMessage(): MessageDto
}
class BadResponseFactory(): ServerResponseFactory(){
    override fun createResponse()=ServerResponseDto(
        createResult()
    )

    override fun createResult()= ResultDto(
        alternativeDtos = listOf(createAlternatives()),
        usageDto = createUsage(),
        modelVersion = "ver. 100"
    )

    override fun createUsage()= UsageDto(
        inputTextTokens = "one",
        completionTokens = "one",
        totalTokens = "two"
    )

    override fun createAlternatives() =
        AlternativeDto(
            messageDto = createMessage(),
            status = "10",
        )

    override fun createMessage() = MessageDto(
        role = "assistant",
        text = ""
    )
}
class GoodResponseFactory(): ServerResponseFactory(){
    override fun createResponse()=ServerResponseDto(
        createResult()
    )

    override fun createResult()= ResultDto(
        alternativeDtos = listOf(createAlternatives()),
        usageDto = createUsage(),
        modelVersion = "1.0"
    )

    override fun createUsage()= UsageDto(
        inputTextTokens = "100",
        completionTokens = "50",
        totalTokens = "150"
    )

    override fun createAlternatives()=
        AlternativeDto(
    messageDto = createMessage(),
    status = "ALTERNATIVE_STATUS_FINAL",
    )

    override fun createMessage() = MessageDto(
    role = "assistant",
    text = "ai response"
    )
}