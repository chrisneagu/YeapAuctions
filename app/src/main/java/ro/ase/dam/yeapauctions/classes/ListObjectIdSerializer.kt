package ro.ase.dam.yeapauctions.classes

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.types.ObjectId
import kotlinx.serialization.descriptors.SerialDescriptor

@Serializer(forClass = LotsWon::class)
object ListObjectIdSerializer : KSerializer<List<ObjectId>> {
    private val objectIdSerializer = ObjectIdSerializer

    override val descriptor: SerialDescriptor =
        ListSerializer(objectIdSerializer).descriptor

    override fun serialize(encoder: Encoder, value: List<ObjectId>) {
        val objectIdList = value.map { it.toHexString() }
        encoder.encodeSerializableValue(ListSerializer(String.serializer()), objectIdList)
    }

    override fun deserialize(decoder: Decoder): List<ObjectId> {
        val objectIdList = decoder.decodeSerializableValue(ListSerializer(String.serializer()))
        return objectIdList.map { ObjectId(it) }
    }
}
