package ru.bjcreslin.naidizakupku.telegram.events.impl

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.bjcreslin.naidizakupku.telegram.events.CallbackEvent

@Service
class CallbackEventImpl : CallbackEvent {
    override fun action(update: Update): SendMessage {
        TODO("Not yet implemented")
    }
}