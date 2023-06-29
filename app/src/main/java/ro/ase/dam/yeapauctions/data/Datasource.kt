package ro.ase.dam.yeapauctions.data

import KtorHttpClient
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.request.*
import io.ktor.client.call.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.ktor.client.statement.*
import io.ktor.http.*
import org.bson.types.ObjectId
import ro.ase.dam.yeapauctions.classes.*
import ro.ase.dam.yeapauctions.ktor.SocketHandler
import io.socket.client.Socket
import kotlinx.coroutines.CompletableDeferred

object Datasource {
    var auctions: List<Auction> by mutableStateOf(emptyList())
    var addresses: List<Address> by mutableStateOf(emptyList())
    var descriptions: List<Description> by mutableStateOf(emptyList())
    var lots: List<Lot> by mutableStateOf(emptyList())
    var offers: List<Offer> by mutableStateOf(emptyList())
    var auctionLots: List<Lot> by mutableStateOf(emptyList())
    var address : Address by mutableStateOf(Address())
    var lotLink : Link by mutableStateOf(Link())
    var links : List<Link> by mutableStateOf(emptyList())
    lateinit var clientSocket : Socket

    fun initializeSocket(){
        SocketHandler.setSocket()
        clientSocket = SocketHandler.getSocket()
        clientSocket.connect()
    }

    suspend fun submitOffer(lot: Lot, userId: String, amount: Double) {
        try {
            val auctionCost = "%.2f".format(amount * 0.18).toDouble()
            val vat = "%.2f".format(amount * lot.vat / 100).toDouble()
            val auctionVAT = "%.2f".format(amount * 0.18 * 0.19).toDouble()
            val total = "%.2f".format(amount + auctionCost + vat + auctionVAT).toDouble()
            val offer = Offer(
                auctionId = lot.auctionId,
                lotId = lot.id,
                userId = ObjectId(userId),
                amount = amount,
                VAT = vat,
                markup = auctionCost,
                markupVAT = auctionVAT,
                total = total
            )
            KtorHttpClient.post("/api/newOffer"){
                contentType(ContentType.Application.Json)
                setBody(offer)
            }
            var lotOffers = offers.filter { it.lotId == lot.id }
            var maxOffer = lotOffers.maxByOrNull { it.amount }

            val user : User = KtorHttpClient.get("/api/users/$userId").body();
            sendEmail(
                user.email,
                "Bid confirmation: " + lot.name,
                user.firstName.first() + ". " + user.lastName +", you just placed an offer for lot " + lot.number + ", " + lot.name +
                        ", in value of " + amount + "$ !"
            )
            if(maxOffer != null){
                val outbidedUser : User = KtorHttpClient.get("/api/users/${maxOffer.userId}").body();
                sendEmail(
                    outbidedUser.email,
                    "Outbid: " + lot.name,
                    outbidedUser.firstName.first() + ". " + outbidedUser.lastName +", you just have been outbid " + lot.number + ", " + lot.name +
                            ", by an offer of " + amount + "$ !"
                )
            }
        }
        catch(e: Exception){
            Log.e("createOffer", "Failed to create offer to DB: ${e.message}", e)
        }
    }

    suspend fun sendEmail(email: String, subject:String, content: String){
        try{
            KtorHttpClient.post("/api/sendEmail"){
                contentType(ContentType.Application.Json)
                setBody("{\n" +
                        "    \"email\":\"${email}\",\n" +
                        "    \"subject\":\"${subject}\",\n" +
                        "    \"content\":\"${content} \"\n" +
                        "}")
            }
        }catch (e: Exception){
            Log.e("sendingEmail", "Failed to send email:  ${e.message}", e)
        }
    }

    suspend fun loadLink(userId: String, lot: Lot){
        try {
            lotLink  = KtorHttpClient.get("/api/links/" + userId + "/" + lot.id).body()
        } catch (e: Exception) {
            Log.e("loadLink", "Failed to load link from DB: ${e.message}", e)
            lotLink.userId = ObjectId(userId);
            lotLink.lotId = lot.id;
            try{
                KtorHttpClient.post("/api/newLink"){
                    contentType(ContentType.Application.Json)
                    setBody(lotLink)
                }
            }
            catch(e: Exception){
                Log.e("createLink", "Failed to create link to DB: ${e.message}", e)
            }
        }
    }

    suspend fun loadAddress(auction: Auction){
        try {
            address  = KtorHttpClient.get("/api/addresses/" + auction.addressId).body()
        } catch (e: Exception) {
            Log.e("loadAddress", "Failed to load address from DB: ${e.message}", e)
        }
    }

    suspend fun loadAuctionLots(auction: Auction){
        try {
            auctionLots  = KtorHttpClient.get("/api/lots/auction/" + auction.id).body()
        } catch (e: Exception) {
            Log.e("loadAuctionLots", "Failed to load auction lots from DB: ${e.message}", e)
        }
    }

    suspend fun loadAuctions(){
        try {
            auctions  = KtorHttpClient.get("/api/auctions").body()
        } catch (e: Exception) {
            Log.e("loadAuctions", "Failed to load auctions from DB: ${e.message}", e)
        }
    }
    suspend fun loadAddresses(){
        try {
            addresses  = KtorHttpClient.get("/api/addresses").body()
        } catch (e: Exception) {
            Log.e("loadAddresses", "Failed to load addresses from DB: ${e.message}", e)
        }
    }
    suspend fun loadDescriptions(){
        try {
            descriptions  = KtorHttpClient.get("/api/descriptions").body()
        } catch (e: Exception) {
            Log.e("loadDescriptions", "Failed to load descriptions from DB: ${e.message}", e)
        }
    }
    suspend fun loadLots(){
        try {
            lots = KtorHttpClient.get("/api/lots").body()
        } catch (e: Exception) {
            Log.e("loadLots", "Failed to load lots from DB: ${e.message}", e)
        }
    }
    suspend fun loadOffers(){
        try{
            offers = KtorHttpClient.get("/api/offers").body()
        }catch (e: Exception) {
            Log.e("loadOffers", "Failed to load offers from DB: ${e.message}", e)
        }
    }

    suspend fun loadLinks() {
        try{
            links = KtorHttpClient.get("/api/links").body()
        }catch (e: Exception) {
            Log.e("loadLinks", "Failed to load links from DB: ${e.message}", e)
        }
    }
}