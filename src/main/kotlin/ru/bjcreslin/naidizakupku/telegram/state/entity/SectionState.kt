package ru.bjcreslin.naidizakupku.telegram.state.entity

/**
 * в какой секции телеграм находится пользователь
 */
enum class SectionState(val key: String) {
    MAIN_MENU("root"),
    GIGACHAT("gigachat"),
}