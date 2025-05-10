package ru.bjcreslin.naidizakupku.gigachat.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GigaChatModel(
    val id: String,
    @JsonProperty("object")
    val obj: String,
    @JsonProperty("owned_by")
    val ownedBy: String,
    val type: String
)