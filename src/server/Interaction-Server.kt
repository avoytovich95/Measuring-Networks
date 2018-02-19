package server

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*
import java.util.*

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

private fun udp(){
    var rep = 0
    var byteArray = ByteArray(1)
    println("(1) 256*4KB (2) 512*2KB (3) 1024*1KB")
    print("Select option: ")
    when(scan.nextInt()){
        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
        2 -> { byteArray = Data.padArray(2 * 1024); rep = 512 }
        3 -> { byteArray = Data.padArray(1024); rep = 1024}
    }
    println("Starting server")
    var socket: DatagramSocket
    var packet: DatagramPacket
    val ack = Data.padArray(1)
    var outPacket: DatagramPacket

    while(true){
        socket = DatagramSocket(Data.port)
        packet = DatagramPacket(byteArray, byteArray.size)

        var count = 0
        var msg = 0
        println("Waiting for packets...")
        while(true){
            try {
//                for (i in 1..rep) {
//                    socket.receive(packet)
//                    if (count == 0 && msg == 0) println("Receiving packets...")
//                    if (Data.checkArray(packet.data, byteArray))
//                        msg++
//                    println(i)
//                }
                while(true){
                    socket.receive(packet)
                    if(packet.data == null)
                        break
                    if(Data.checkArray(packet.data, byteArray))
                        msg++
                    println(msg)
                }
                println(packet.address)
                println(packet.port)
                outPacket = DatagramPacket(ack, ack.size, packet.address, packet.port)
                socket.send(outPacket)
                if (msg == rep)
                    print("Y")
                else print("N")
                msg = 0

                socket.soTimeout = 2000
                count++
            }catch (e: SocketTimeoutException){
                println("\nReceived $count packets.")
                println("Socket timed out!")
                socket.soTimeout = 0
                break
            }
        }
        println("Closing connection!\n")
        socket.close()
    }
}