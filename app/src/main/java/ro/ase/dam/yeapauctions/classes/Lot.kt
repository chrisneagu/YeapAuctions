package ro.ase.dam.yeapauctions.classes

import androidx.compose.runtime.saveable.listSaver
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.Date

@Serializable
data class Lot(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    var id: ObjectId = ObjectId(),
    val number: Int = 1,
    var name: String = "",
    var startingPrice: Double = 0.0,
    @Contextual
    @Serializable(with = DateSerializer::class)
    var startTime: Date = Date(),
    @Contextual
    @Serializable(with = DateSerializer::class)
    var endTime: Date = Date(),
    var category: String = "",
    var subcategory: String = "",
    var vat: Double = 0.0,
    @SerialName("auctionId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    var auctionId: ObjectId = ObjectId(),
    @SerialName("descriptionId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    var descriptionId: ObjectId = ObjectId(),
    var lastFive: Int = 0,
    var lastTwo: Int = 0,
    var verifiedWinner: Boolean = false
)

val LotSaver = listSaver<Lot, Any>(
    save = { listOf(
        it.id, it.number, it.name, it.startingPrice,
        it.startTime, it.endTime, it.category,
        it.subcategory, it.vat, it.auctionId,
        it.descriptionId, it.lastFive, it.lastTwo, it.verifiedWinner
    ) },
    restore = { Lot(
        it[0] as ObjectId, it[1] as Int, it[2] as String,
        it[3] as Double, it[4] as Date, it[5] as Date,
        it[6] as String, it[7] as String, it[8] as Double,
        it[9] as ObjectId, it[10] as ObjectId, it[11] as Int,
        it[12] as Int, it[13] as Boolean
    ) }
)

val LotListSaver = listSaver<List<Lot>?, Any>(
    save = { lots ->
        lots?.flatMap { lot ->
            listOf(
                lot.id, lot.number, lot.name, lot.startingPrice, lot.startTime,
                lot.endTime, lot.category, lot.subcategory,
                lot.vat, lot.auctionId, lot.descriptionId,
                lot.lastFive, lot.lastTwo, lot.verifiedWinner
            )
        }
            ?: listOf()
    },
    restore = { data ->
        data.chunked(14) { chunk ->
            Lot(
                chunk[0] as ObjectId, chunk[1] as Int, chunk[2] as String,
                chunk[3] as Double, chunk[4] as Date, chunk[5] as Date,
                chunk[6] as String, chunk[7] as String, chunk[8] as Double,
                chunk[9] as ObjectId, chunk[10] as ObjectId,
                chunk[11] as Int, chunk[12] as Int, chunk[13] as Boolean
            )
        }
    }
)