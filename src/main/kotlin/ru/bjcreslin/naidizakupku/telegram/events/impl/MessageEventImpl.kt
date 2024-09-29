package ru.bjcreslin.naidizakupku.telegram.events.impl

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.bjcreslin.naidizakupku.telegram.events.MessageEvent
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler

@Service
class MessageEventImpl(
    private val commandServices: Map<String, CommandHandler>
) : MessageEvent {

    override fun action(update: Update): SendMessage {
        val messageText = update.message.text
        val chatId = update.message.chatId

        val message = SendMessage()
        message.chatId = chatId.toString()

        val commandService = commandServices[messageText]
        if (commandService != null) {
            val response = commandService.execute(chatId, messageText)
            message.text = response
        } else {
            message.text = "Неизвестная команда"
        }
        return message
    }

}