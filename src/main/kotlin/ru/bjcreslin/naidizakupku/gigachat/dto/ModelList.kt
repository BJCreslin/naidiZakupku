package ru.bjcreslin.naidizakupku.gigachat.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ModelList(
    @JsonProperty("object")
    val obj: String,
    val data: List<Model>
)