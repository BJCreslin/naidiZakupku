package ru.bjcreslin.naidizakupku.telegram.state.entity

/**
 * в какой секции телеграм находится пользователь
 */
enum class SectionState(val key: String) {
    ROOT("root"),
    GIGACHAT("gigachat"),
}
