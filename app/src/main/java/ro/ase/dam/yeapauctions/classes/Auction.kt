package ro.ase.dam.yeapauctions.classes

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Auction(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    val number: Int = 1,
    val name: String = "",
    val seller: String = "",
    @SerialName("addressId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val addressId: ObjectId = ObjectId(),
    val generatedPayments: Boolean = false
)

val AuctionSaver = listSaver<Auction, Any>(
    save = {
        listOf(
            it.id, it.number, it.name,
            it.seller, it.addressId, it.generatedPayments
        ) },
    restore = { Auction(
        it[0] as ObjectId, it[1] as Int,
        it[2] as String, it[3] as String, it[4] as ObjectId,
        it[5] as Boolean
    ) }
)

val AuctionListSaver = listSaver<List<Auction>?, Any>(
    save = { auctions ->
        auctions?.flatMap { auction ->
            listOf(
                auction.id, auction.number, auction.name,
                auction.seller, auction.addressId, auction.generatedPayments
            )
        }
            ?: listOf()
    },
    restore = { data ->
        data.chunked(14) { chunk ->
            Auction(
                chunk[0] as ObjectId, chunk[1] as Int,
                chunk[2] as String, chunk[3] as String, chunk[4] as ObjectId,
                chunk[5] as Boolean
            )
        }
    }
)