package de.visualdigits.photosite.domain.serializer

import de.visualdigits.photosite.domain.model.type.Language
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LanguageDeserializer : KSerializer<Language> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Language",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Language {
        return Language.fromString(decoder.decodeString()) ?: Language.DE
    }

    override fun serialize(
        encoder: Encoder,
        value: Language
    ) {
        encoder.encodeString(value.localeCode)
    }
}
