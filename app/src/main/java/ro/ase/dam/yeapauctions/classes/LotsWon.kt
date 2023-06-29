package ro.ase.dam.yeapauctions.classes

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable(with = ListObjectIdSerializer::class)
data class LotsWon(val lots: List<ObjectId>)