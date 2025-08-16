package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.GigachatService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState

@Service("root#prompt")
class GigachatPromptHandler(
    private val gigachatService: GigachatService
) : CommandHandler {
    
    override fun execute(chatId: Long, params: String): String {
        return if (params.isNotBlank()) {
            try {
                val response = gigachatService.sendMessage(params)
                response.choices.firstOrNull()?.message?.content ?: "Не удалось получить ответ от GigaChat"
            } catch (e: Exception) {
                "❌ Ошибка при обращении к GigaChat: ${e.message}"
            }
        } else {
            """
            🤖 *GigaChat Prompt*
            
            Отправьте ваш вопрос или запрос после команды /prompt
            
            Примеры:
            /prompt Расскажи о госзакупках
            /prompt Как участвовать в тендерах?
            /prompt Анализируй закупки
            
            Или используйте /gigachat для перехода в интерактивный режим
            """.trimIndent()
        }
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }
}