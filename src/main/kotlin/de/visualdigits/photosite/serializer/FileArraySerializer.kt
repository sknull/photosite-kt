package de.visualdigits.photosite.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

class FileArraySerializer : KSerializer<Array<File>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "FileArraySerializer",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Array<File> {
        return decoder.decodeString()
            .split(",")
            .map { File(it.trim()) }
            .toTypedArray()
    }

    override fun serialize(encoder: Encoder, value: Array<File>) {
        encoder.encodeString(value.joinToString(",") { it.canonicalPath })
    }
}
