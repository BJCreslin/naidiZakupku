package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.user.repository.UserRepository

@Service("root#users")
class UsersListBotHandler(
    val userRepository: UserRepository
) : CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        return userRepository.findAll().joinToString("\n")
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }
}