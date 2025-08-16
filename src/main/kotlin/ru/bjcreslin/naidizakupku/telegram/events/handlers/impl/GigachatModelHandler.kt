package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.GigachatService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState

@Service("root#model")
class GigachatModelHandler(
    private val gigachatService: GigachatService
) : CommandHandler {
    
    override fun execute(chatId: Long, params: String): String {
        val models = gigachatService.getModels()
        return buildString {
            appendLine("🤖 *Доступные модели GigaChat:*")
            appendLine()
            models.forEachIndexed { index, model ->
                appendLine("${index + 1}. `$model`")
            }
            appendLine()
            appendLine("Используйте команду /gigachat для перехода в режим работы с AI")
        }
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }
}