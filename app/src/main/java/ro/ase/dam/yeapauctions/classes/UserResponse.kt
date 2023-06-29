package ro.ase.dam.yeapauctions.classes

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @Serializable(with = ObjectIdSerializer::class)
    val user: User
)