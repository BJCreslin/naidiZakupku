package ru.bjcreslin.naidizakupku.telegram.state.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TelegramSectionUserRepository : JpaRepository<TelegramSectionUser, Long> {

}