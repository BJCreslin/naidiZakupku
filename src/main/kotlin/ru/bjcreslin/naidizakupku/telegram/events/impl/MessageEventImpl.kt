package ru.bjcreslin.naidizakupku.telegram.events.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.bjcreslin.naidizakupku.telegram.events.MessageEvent
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.events.handlers.impl.ProcurementsListBotService
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegram.state.service.TelegramStateService

@Service
class MessageEventImpl(
    private val commandServices: Map<String, CommandHandler>,
    private val stateService: TelegramStateService,
    private val procurementsListService: ProcurementsListBotService
) : MessageEvent {

    private val logger = LoggerFactory.getLogger(MessageEventImpl::class.java)

    override fun action(update: Update): SendMessage {
        val messageText = update.message.text
        val chatId = update.message.chatId
        val username = update.message.from?.userName ?: "unknown"

        logger.info("Processing message from user $username (chatId: $chatId): $messageText")

        try {
            return when {
                messageText.isBlank() -> {
                    SendMessage().apply {
                        this.chatId = chatId.toString()
                        text = "Пожалуйста, введите команду. Используйте /help для списка доступных команд."
                        enableMarkdown(true)
                    }
                }
                !messageText.startsWith("/") -> {
                    SendMessage().apply {
                        this.chatId = chatId.toString()
                        text = handleNonCommandMessage(chatId, messageText)
                        enableMarkdown(true)
                    }
                }
                messageText == "/list" -> {
                    // Специальная обработка для команды /list с кнопками
                    procurementsListService.executeWithPagination(chatId, 0)
                }
                else -> {
                    val key = stateService.getCommandHandlerKey(chatId, messageText)
                    val commandService = commandServices[key]
                    
                    if (commandService != null) {
                        val response = commandService.execute(chatId, messageText)
                        SendMessage().apply {
                            this.chatId = chatId.toString()
                            text = response
                            enableMarkdown(true)
                        }.also {
                            logger.info("Command executed successfully for user $username")
                        }
                    } else {
                        SendMessage().apply {
                            this.chatId = chatId.toString()
                            text = "Неизвестная команда: $messageText\nИспользуйте /help для списка доступных команд."
                            enableMarkdown(true)
                        }.also {
                            logger.warn("Unknown command '$messageText' from user $username")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing message from user $username: ${e.message}", e)
            return SendMessage().apply {
                this.chatId = chatId.toString()
                text = "Произошла ошибка при обработке команды. Попробуйте позже или обратитесь к администратору."
                enableMarkdown(true)
            }
        }
    }

    private fun handleNonCommandMessage(chatId: Long, messageText: String): String {
        val currentState = stateService.getState(chatId)
        return when (currentState) {
            SectionState.GIGACHAT -> "Обрабатываю запрос в режиме GigaChat..."
            else -> "Для работы с ботом используйте команды, начинающиеся с '/'. Введите /help для списка команд."
        }
    }
}