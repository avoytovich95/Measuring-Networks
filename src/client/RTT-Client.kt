package client

import java.io.*
import java.net.*
import java.util.*

/**
 * Created by Alex on 2/1/2018.
 */
fun main(args: Array<String>){
    val scan = Scanner(System.`in`)
    print("Select server type: ")
        val type = scan.nextLine()
    print("Select package size (Bytes): ")
        val length = scan.nextInt()
    print("Enter package count: ")
        val max = scan.nextInt()
    when(type){
        "tcp" -> tcp(length, max)
        "udp" -> udp(length, max)
    }
}

private fun tcp(length: Int, max: Int){
    var socket: Socket? = null
    var output: ObjectOutputStream? = null
    var input: ObjectInputStream? = null

    //Establishing connection
    try {
        socket = Socket(Data.address, Data.port)
        output = ObjectOutputStream(socket.getOutputStream())
        input = ObjectInputStream(socket.getInputStream())
        //println("IO established!")

    } catch (e: UnknownHostException) {
        println("Couldn't get host")
        e.printStackTrace()
        System.exit(1)
    } catch (e: IOException) {
        println("Couldn't get IO from connection!")
        e.printStackTrace()
        System.exit(1)
    }

    //Sending message
    try {
        val bytes = Data.padArray(length)

        var loops = 0
        var start: Long
        var end: Long
        var byteIn: Any?
        var correctness = 0
        while(loops < max) {
            start = System.nanoTime()
            output?.writeObject(bytes)
            output?.flush()
            byteIn = input?.readObject()
            end = System.nanoTime() - start

            //print("${loops + 1}: ")
            Data.timeConvert(end)
            if(Data.checkArray(bytes, byteIn as ByteArray))
                correctness++
            loops++
        }
        println("$correctness echoes correct")
        input?.close()
        output?.close()
        socket?.close()

    } catch (e: IOException) {
        println("IO Failure")
        e.printStackTrace()
    }
}

private fun udp(length: Int, max: Int){
    val socket = DatagramSocket()
    val ip = InetAddress.getByName(Data.address)

    var loop = 0
    val bytes = Data.padArray(length)
    val packet = DatagramPacket(bytes, length, ip, Data.port)

    var start: Long
    var end: Long
    var correctness = 0
    while(loop < max) {
        start = System.nanoTime()
        socket.send(packet)
        socket.receive(packet)

        end = System.nanoTime() - start
        //print("${loop + 1}: ")
        Data.timeConvert(end)
        if(Data.checkArray(bytes, packet.data))
            correctness++

        loop ++
    }
    println("$correctness echoes correct")
    socket.close()
}