package ru.bjcreslin.naidizakupku.gigachat

enum class GigaChatModel(val value: String) {
    GIGACHAT("GigaChat"),
    GIGACHAT_2("GigaChat-2"),
    GIGACHAT_2_MAX("GigaChat-2-Max"),
    GIGACHAT_2_MAX_PREVIEW("GigaChat-2-Max-preview"),
    GIGACHAT_2_PRO("GigaChat-2-Pro"),
    GIGACHAT_2_PRO_PREVIEW("GigaChat-2-Pro-preview"),
    GIGACHAT_2_PREVIEW("GigaChat-2-preview"),
    GIGACHAT_MAX("GigaChat-Max"),
    GIGACHAT_MAX_PREVIEW("GigaChat-Max-preview"),
    GIGACHAT_PLUS("GigaChat-Plus"),
    GIGACHAT_PLUS_PREVIEW("GigaChat-Plus-preview"),
    GIGACHAT_PRO("GigaChat-Pro"),
    GIGACHAT_PRO_PREVIEW("GigaChat-Pro-preview"),
    GIGACHAT_PREVIEW("GigaChat-preview"),
    EMBEDDINGS("Embeddings"),
    EMBEDDINGS_2("Embeddings-2"),
    EMBEDDINGS_GIGAR("EmbeddingsGigaR");

    companion object {
        fun fromValue(value: String): GigaChatModel? {
            return values().find { it.value.equals(value, ignoreCase = true) }
        }
    }
}