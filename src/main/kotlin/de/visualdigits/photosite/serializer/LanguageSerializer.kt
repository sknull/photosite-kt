package de.visualdigits.photosite.serializer

import de.visualdigits.photosite.model.common.Language
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class LanguageSerializer : KSerializer<Language> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Language",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Language {
        return Language(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Language) {
        encoder.encodeString(value.language)
    }
}
