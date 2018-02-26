package server

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.sign

private val scan = Scanner(System.`in`)

/**
 * Created by Alex on 2/16/2018.
 */
fun main(args: Array<String>){
    print("Select server type: ")
    when(scan.nextLine()){
        "tcp" -> tcp()
        "udp" -> udp()
    }
}

private fun tcp(){
    var rep = 0
    var byteArray = ByteArray(1)
    println("(1) 256*4KB (2) 512*2KB (3) 1024*1KB")
    print("Select option: ")
    when(scan.nextInt()){
        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
        2 -> { byteArray = Data.padArray(2 * 1024); rep = 512 }
        3 -> { byteArray = Data.padArray(1024); rep = 1024}
    }
    val serverSocket = ServerSocket(Data.port)
    var client: Socket
    var input: ObjectInputStream
    var output: ObjectOutputStream
    val ack = Data.padArray(1)

    println("Starting server")
    while(true){
        println("Waiting for a connection...")
        client = serverSocket.accept()
        println("Client $client has connected")

        input = ObjectInputStream(client.getInputStream())
        output = ObjectOutputStream(client.getOutputStream())
        println("IO established")

        var count = 0
        var msg = 0
        while(true){
            try{
                if(count == 0) println("Receiving messages")
                for(i in 1..rep){
                    if(Data.checkArray(input.readObject() as ByteArray, byteArray))
                        msg++
                }
                output.writeObject(ack)
                count++
                if(msg == rep)
                    print("Y")
                else print("N")
                msg = 0

            }catch (e: IOException){
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

private fun udp() {
    var rep = 0
    var byteArray = ByteArray(1)
    println("(1) 256*4KB (2) 512*2KB (3) 1024*1KB")
    print("Select option: ")
    when(scan.nextInt()){
        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
        2 -> { byteArray = Data.padArray(2 * 1024); rep = 512 }
        3 -> { byteArray = Data.padArray(1024); rep = 1024}
    }
    println("Starting server...")
    var socket: DatagramSocket
    var packet: DatagramPacket

    val ack = Data.padArray(1)
    val outPacket = DatagramPacket(ack, ack.size)
    var count = 0
    var msg = 0
    while(true){
        socket = DatagramSocket(Data.port)
        packet = DatagramPacket(byteArray, byteArray.size)

        while (true) {
            try {
                println("Waiting for packets...")
                for (i in 1..rep) {
                    socket.receive(packet)
                    if (Data.checkArray(packet.data, byteArray)) {
                        msg++
                        print("$i ")
                    }
                }
                socket.soTimeout = 2000
                outPacket.address = packet.address
                outPacket.port = packet.port
                socket.send(outPacket)

                if (msg == rep)
                    println("\nY")
                else println("\nN")
                msg = 0
                count++
            } catch (e: SocketTimeoutException) {
                println("Socket times out!")
                println("Received $count messages")
                count = 0
                socket.soTimeout = 0

                println("Closing connection!\n")
                socket.close()
                break
            }
        }
    }
}

//private fun udp(){
//    var rep = 0
//    var byteArray = ByteArray(1)
//    println("(1) 256*4KB (2) 512*2KB (3) 1024*1KB")
//    print("Select option: ")
//    when(scan.nextInt()){
//        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
//        2 -> { byteArray = Data.padArray(2 * 1024); rep = 512 }
//        3 -> { byteArray = Data.padArray(1024); rep = 1024}
//    }
//    println("Starting server")
//    var socket: DatagramSocket
//    var packet: DatagramPacket
//
//    val ack = ByteArray(1)
//    val outPacket = DatagramPacket(ack, ack.size)
//
//    while(true){
//        socket = DatagramSocket(Data.port)
//        packet = DatagramPacket(byteArray, byteArray.size)
//
//        var count = 0
//        var msg = 0
//        println("Waiting for packets...")
//        while(true){
//            try {
//                for (i in 1..rep) {
//                    try{
//                        socket.receive(packet)
//                        if (msg == 0) socket.soTimeout = 2000
//                        if (Data.checkArray(packet.data, byteArray))
//                            msg++
//                        print("$msg ")
//                        //socket.send(outPacket)
//
//                    }catch (e: SocketTimeoutException){
//                        ack[0] = 0x0
//                        outPacket.data = ack
//                        socket.send(outPacket)
//                    }
//                }
//                ack[0] = 0x1
//                outPacket.data = ack
//                outPacket.port = packet.port
//                outPacket.address = packet.address
//                socket.send(outPacket)
//
//                socket.soTimeout = 2000
//
//                if (msg == rep)
//                    println("\nY")
//                else println("\nN")
//                msg = 0
//                count++
//            }catch (e: SocketTimeoutException){
//
//                println("\nReceived $count packets.")
//                println("Socket timed out!")
//                socket.soTimeout = 0
//                break
//            }
//        }
//        println("Closing connection!\n")
//        socket.close()
//    }
//
//}

