package ru.bjcreslin.naidizakupku.gigachat.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FunctionCall(
    val name: String,
    val arguments: Map<String, Any>
)

data class Message(
    val role: String,
    val content: String,
    val created: Long,
    val name: String?,
    @JsonProperty("functions_state_id")
    val functionsStateId: String?,
    @JsonProperty("function_call")
    val functionCall: FunctionCall?
)

data class Choice(
    val message: Message,
    val index: Int,
    @JsonProperty("finish_reason")
    val finishReason: String
)

data class Usage(
    @JsonProperty("prompt_tokens")
    val promptTokens: Int,
    @JsonProperty("completion_tokens")
    val completionTokens: Int,
    @JsonProperty("precached_prompt_tokens")
    val precachedPromptTokens: Int,
    @JsonProperty("total_tokens")
    val totalTokens: Int
)

data class GigaChatResponse(
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    val usage: Usage,
    val `object`: String
)