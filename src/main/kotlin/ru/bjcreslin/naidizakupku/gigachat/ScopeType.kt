package ru.bjcreslin.naidizakupku.gigachat

/**
 * Версия API. Возможные значения
 */
enum class ScopeType {

    /***  доступ для физических лиц.*/
    GIGACHAT_API_PERS,

    /*** доступ для ИП и юридических лиц по платным пакетам. */
    GIGACHAT_API_B2B,

    /*** доступ для ИП и юридических лиц по схеме pay-as-you-go. */
    GIGACHAT_API_CORP;
}