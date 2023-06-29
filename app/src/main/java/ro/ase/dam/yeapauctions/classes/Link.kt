package ro.ase.dam.yeapauctions.classes

import androidx.compose.runtime.saveable.listSaver
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Link(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    @SerialName("userId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    var userId: ObjectId = ObjectId(),
    @SerialName("auctionId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    var auctionId: ObjectId = ObjectId(),
    @SerialName("lotId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    var lotId: ObjectId = ObjectId(),
    var isFavorite: Boolean = false,
    @Contextual
    @Serializable(with = DateSerializer::class)
    var dateViewed: Date = Date(),
    var won: Boolean = false
)

var LinkSaver = listSaver<Link, Any>(
    save = { listOf(
        it.id, it.userId, it.auctionId,
        it.lotId, it.isFavorite, it.dateViewed, it.won
    ) },
    restore = { Link(
        it[0] as ObjectId, it[1] as ObjectId, it[2] as ObjectId,
        it[3] as ObjectId, it[4] as Boolean, it[5] as Date, it[6] as Boolean
    ) }
)