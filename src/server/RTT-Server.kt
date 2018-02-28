package server

import java.io.*
import java.net.*
import java.util.*

private val scan = Scanner(System.`in`)

/**
 * Created by Alex on 2/1/2018.
 */
fun main(args: Array<String>){
    print("Select server type: ")
    when(scan.nextLine()){
        "tcp" -> tcp()
        "udp" -> udp()
    }
}

private fun tcp(){
    print("Select package size (Bytes): ")
    val byteList = Data.padArray(scan.nextInt())

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
                output.flush()
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

private fun udp(){
    print("Select package size (Bytes): ")
    val byteList = Data.padArray(scan.nextInt())

    println("Starting server")
    var socket: DatagramSocket
    var packet: DatagramPacket

    while(true){
        socket = DatagramSocket(Data.port)
        packet = DatagramPacket(byteList, byteList.size)

        var count = 0
        println("Waiting for packets...")
        while(true) {
            try {
                socket.receive(packet)
                if(count == 0) println("Receiving packets...")
                if(Data.checkArray(byteList, packet.data))
                    print("Y")
                else print("N")

                socket.soTimeout = 2000
                socket.send(packet)
                count++
            }catch(e: SocketTimeoutException){
                println("\nEchoed $count packets.")
                println("Socket timed out!")
                socket.soTimeout = 0
                break
            }
        }
        println("Closing connection!\n")
        socket.close()
    }
}
