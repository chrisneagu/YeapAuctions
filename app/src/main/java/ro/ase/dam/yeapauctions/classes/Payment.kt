package ro.ase.dam.yeapauctions.classes

import androidx.compose.runtime.saveable.listSaver
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.Date

@Serializable
data class Payment(
    @SerialName("_id")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    val number: Int = 0,
    @SerialName("userId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val userId: ObjectId,
    @SerialName("auctionId")
    @Contextual
    @Serializable(with = ObjectIdSerializer::class)
    val auctionId: ObjectId,
    val totalVAT: Double,
    val totalMarkup: Double,
    val totalMarkupVAT: Double,
    val grandTotal: Double,
    var paid: Boolean = false,
    @Contextual
    @Serializable(with = DateSerializer::class)
    val createdAt: Date = Date(),
    @Contextual
    @Serializable(with = DateSerializer::class)
    val dueDate: Date = Date(),
)

val PaymentSaver = listSaver<Payment, Any>(
    save = { listOf(
        it.id, it.number, it.userId, it.auctionId,
        it.totalVAT, it.totalMarkup, it.totalMarkupVAT,
        it.grandTotal, it.paid, it.createdAt, it.dueDate
    ) },
    restore = { Payment(
        it[0] as ObjectId, it[1] as Int, it[2] as ObjectId, it[3] as ObjectId,
        it[4] as Double, it[5] as Double, it[6] as Double,
        it[7] as Double, it[8] as Boolean, it[9] as Date, it[10] as Date
    ) }
)

val PaymentListSaver = listSaver<List<Payment>?, Any>(
    save = { payments ->
        payments?.flatMap { payment ->
            listOf(
                payment.id, payment.number, payment.userId, payment.auctionId,
                payment.totalVAT, payment.totalMarkup, payment.totalMarkupVAT,
                payment.grandTotal, payment.paid, payment.createdAt, payment.dueDate
            )
        }
            ?: listOf()
    },
    restore = { data ->
        data.chunked(11) { chunk ->
            Payment(
                chunk[0] as ObjectId, chunk[1] as Int, chunk[2] as ObjectId, chunk[3] as ObjectId,
                chunk[4] as Double, chunk[5] as Double, chunk[6] as Double,
                chunk[7] as Double, chunk[8] as Boolean, chunk[9] as Date, chunk[10] as Date
            )
        }
    }
)