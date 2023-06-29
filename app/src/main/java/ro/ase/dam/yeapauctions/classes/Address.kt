package ro.ase.dam.yeapauctions.classes

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Address(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    val city: String = "",
    val country: String = "",
    val zipCode: String = "",
    val street: String = "",
    val number: String = ""
)