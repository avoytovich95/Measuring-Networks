package server

import java.io.*
import java.net.*
import java.util.*

/**
 * Created by Alex on 2/8/2018.
 */
fun main(args: Array<String>){
    val scan = Scanner(System.`in`)
    print("Select package size (KB): ")
    val byteList = Data.padArray(scan.nextInt() * 1024)

    val serverSocket = ServerSocket(Data.port)
    var client: Socket
    var input: ObjectInputStream
    var output: ObjectOutputStream
    var bytes: Any

    println("Starting server")
    while(true){
        println("Waiting for a connection...")
        client = serverSocket.accept()
        println("Client $client has connected")

        input = ObjectInputStream(client.getInputStream())
        output = ObjectOutputStream(client.getOutputStream())
        println("IO established!")

        var count = 0
        while(true) {
            try {
                if(count == 0) println("Receiving messages!")
                bytes = input.readObject()
                if(Data.checkArray(byteList, bytes as ByteArray))
                    print("Y")
                else print("N")

                output.writeObject(bytes)
                count++
            }catch(e: IOException){
                println("\nReceived $count messages.")
                println("No more data received!")
                break
            }
        }
        println("Closing connection!\n")
        input.close()
        output.close()
        client.close()
    }
}