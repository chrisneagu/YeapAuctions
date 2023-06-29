package ro.ase.dam.yeapauctions.ktor

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {
    lateinit var clientSocket: Socket

    @Synchronized
    fun setSocket(){
        try{
            clientSocket = IO.socket("http://192.168.0.183:5001")
        }catch(e: URISyntaxException){

        }
    }

    @Synchronized
    fun getSocket(): Socket{
        return clientSocket
    }
}