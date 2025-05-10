package ru.bjcreslin.naidizakupku.gigachat.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GigaChatRequest(
    val gigaChatModel: GigaChatModel,
    val messages: List<GigaChatMessage>,
    val stream: Boolean,
    @JsonProperty("update_interval")
    val updateInterval: Long
)


data class GigaChatMessage(
    val role: GigaChatRole,
    val content: String
)

enum class GigaChatRole() {
    system,
    user
}
