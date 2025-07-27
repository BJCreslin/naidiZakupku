package ru.bjcreslin.naidizakupku.common.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.math.BigDecimal

/**
 * Десериализатор для BigDecimal с пробелами
 */
class BigDecimalWithSpaceDeserializer : JsonDeserializer<BigDecimal>() {

    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): BigDecimal? {
        if (ctxt == null) return null
        val numberString = p!!.text.replace(" ", "").replace(",", ".")
        return BigDecimal(numberString)
    }
}