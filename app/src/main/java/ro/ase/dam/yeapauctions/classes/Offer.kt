package ro.ase.dam.yeapauctions.classes

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.Date

@Serializable
data class Offer(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    @SerialName("auctionId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val auctionId: ObjectId = ObjectId(),
    @SerialName("lotId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val lotId: ObjectId = ObjectId(),
    @SerialName("userId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val userId: ObjectId = ObjectId(),
    val amount: Double = 0.0,
    val VAT: Double = 0.0,
    val markup: Double = 0.0,
    val markupVAT: Double = 0.0,
    val total: Double = 0.0,
    @Contextual
    @Serializable(with = DateSerializer::class)
    val dateCreated: Date = Date(),
    val doneWithAutoBid: Boolean = false,
    val autobidOn: Boolean = false,
    val maximumAmount: Double = 0.0
)

