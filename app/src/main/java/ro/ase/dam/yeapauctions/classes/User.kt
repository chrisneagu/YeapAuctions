package ro.ase.dam.yeapauctions.classes

import androidx.compose.runtime.saveable.listSaver
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class User(
    @Serializable(with = ObjectIdSerializer::class)
    @SerialName("_id")
    @Contextual
    val id: ObjectId = ObjectId(),
    val number: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val gender: Boolean = false,
    val isCompany: Boolean = false,
    val companyName: String? = if (isCompany) "" else null,
    val vatCode: String? = if (isCompany) "" else null,
    val industry: String? = if (isCompany) "" else null,
    @Serializable(with = ObjectIdSerializer::class)
    @SerialName("addressId")
    @Contextual
    val addressId: ObjectId = ObjectId()
)