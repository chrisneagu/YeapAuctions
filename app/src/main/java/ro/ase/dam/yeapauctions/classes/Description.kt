package ro.ase.dam.yeapauctions.classes

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Description(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    val condition: String = "",
    val status: String = "",
    val appearance: String = "",
    val packaging: String = "",
    val quantity: Int = 0,
    val informations: String = "",
    val imagePaths: String = ""
)

