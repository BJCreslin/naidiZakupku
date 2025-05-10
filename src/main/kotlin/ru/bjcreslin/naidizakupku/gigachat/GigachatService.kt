package ru.bjcreslin.naidizakupku.gigachat

import chat.giga.client.GigaChatClient
import chat.giga.client.auth.AuthClient
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder
import chat.giga.model.ModelName
import chat.giga.model.Scope
import chat.giga.model.completion.ChatMessage
import chat.giga.model.completion.ChatMessageRole
import chat.giga.model.completion.CompletionRequest
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.GigaChatConfiguration


@Service
class GigachatService(val chatConfiguration: GigaChatConfiguration) {
    private val Logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    fun getModels(chatId: Long): GigaChatClient? {
        val client: GigaChatClient? = GigaChatClient.builder()
            .authClient(
                AuthClient.builder()
                    .withOAuth(
                        OAuthBuilder.builder()
                            .scope(Scope.GIGACHAT_API_PERS)
                            .clientId(chatConfiguration.clientId)
                            .clientSecret(chatConfiguration.clientSecret)
                            .build()
                    )
                    .build()
            )
            .logRequests(true)
            .logResponses(true)
            .build()
        Logger.debug("User $chatId trying to get list of models. Balance: ${client?.balance().toString()}")
        return client
    }

    fun getAnswer(text: String): String {
        val client: GigaChatClient? = GigaChatClient.builder()
            .verifySslCerts(false)
            .authClient(
                AuthClient.builder()
                    .withOAuth(
                        OAuthBuilder.builder()
                            .scope(Scope.GIGACHAT_API_PERS)
                            .clientId(chatConfiguration.clientId)
                            .clientSecret(chatConfiguration.clientSecret)
                            .build()
                    )
                    .build()
            )
            .build()
        val response = client?.completions(
            CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .message(
                    ChatMessage.builder()
                        .content(text)
                        .role(ChatMessageRole.USER)
                        .build()
                )
                .build()
        )
        Logger.info("Response from gigachat: ${response.toString()}")
        return response.toString()
    }
}