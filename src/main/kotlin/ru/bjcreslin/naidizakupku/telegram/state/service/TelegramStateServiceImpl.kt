package ru.bjcreslin.naidizakupku.telegram.state.service

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegram.state.entity.TelegramSectionUser
import ru.bjcreslin.naidizakupku.telegram.state.entity.TelegramSectionUserRepository

@Service
class TelegramStateServiceImpl(val repository: TelegramSectionUserRepository) : TelegramStateService {

    override fun getState(chatID: Long): SectionState {
        val telegramState = repository.findByTelegramId(chatID)
        if (telegramState == null) {
            return SectionState.ROOT
        }
        return telegramState.sectionState
    }

    override fun getCommandHandlerKey(chatID: Long, messageText: String): String {
        val telegramState = getState(chatID)
        return "${telegramState.key}#${messageText.removePrefix("/")}"
    }

    override fun setState(
        chatID: Long,
        sectionState: SectionState
    ) {
        val telegramState = repository.findByTelegramId(chatID)
        if (telegramState == null) {
            repository.save(
                TelegramSectionUser(
                    0,
                    chatID,
                    sectionState
                )
            )
        } else {
            if (telegramState.sectionState != sectionState) {
                telegramState.sectionState = sectionState
                repository.save(telegramState)
            }
        }
    }
}