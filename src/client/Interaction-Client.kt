package client

import java.io.*
import java.net.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.xml.stream.events.EndDocument

/**
 * Created by Alex on 2/16/2018.
 */
fun main(args: Array<String>){
    val scan = Scanner(System.`in`)
    print("Select server type: ")
        val type = scan.nextLine()
    println("(1) 256*4KB (2) 512*2KB (3) 1024*1KB")
    print("Select option: ")
        val option = scan.nextInt()
    print("Enter message count: ")
        val loop = scan.nextInt()
    when(type){
        "tcp" -> tcp(option, loop)
        "udp" -> udp(option, loop)
    }
}

private fun tcp(option: Int, max: Int){
    var socket: Socket? = null
    var output: ObjectOutputStream? = null
    var input: ObjectInputStream? = null

    //Establish connection
    try {
        socket = Socket(Data.address, Data.port)
        output = ObjectOutputStream(socket.getOutputStream())
        input = ObjectInputStream(socket.getInputStream())
    } catch (e: UnknownHostException) {
        println("Couldn't get host")
        e.printStackTrace()
        System.exit(1)
    } catch (e: IOException) {
        println("Couldn't get IO from connection!")
        e.printStackTrace()
        System.exit(1)
    }

    var rep = 0
    var byteArray = ByteArray(1)
    when (option) {
        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
        2 -> { byteArray = Data.padArray(2 * 1024); rep = 512 }
        3 -> { byteArray = Data.padArray(1024); rep = 1024 }
    }
    try{
        var loops = 0
        var start: Long
        var end: Long
        var byteIn: Any? = null
        val ack = Data.padArray(1)
        var correct = 0

        while(loops < max){
            start = System.nanoTime()
            for(i in 1..rep){
                output?.writeObject(byteArray)
                output?.flush()
                byteIn = input?.readObject()
            }
            end = System.nanoTime() - start
            Data.timeConvert(end)
            if(Data.checkArray(byteIn as ByteArray, ack))
                correct++

            loops++
        }
        println("$correct responses correct")
        input?.close()
        output?.close()
        socket?.close()

    }catch (e: IOException) {
        println("IO Failure")
        e.printStackTrace()
    }
}

private fun udp(option: Int, max: Int) {
    val socket = DatagramSocket()
    val ip = InetAddress.getByName(Data.address)


    var rep = 0
    var byteArray = ByteArray(1)
    when(option){
        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
        2 -> { byteArray = Data.padArray(2 * 1024); rep = 523 }
        3 -> { byteArray = Data.padArray(1024); rep = 1024}
    }

    var loop = 0
    val packet = DatagramPacket(byteArray, byteArray.size, ip, Data.port)
    val ack = ByteArray(1)
    ack[0] = 0x1
    val inPacket = DatagramPacket(ack, ack.size, ip, Data.port)

    var start: Long
    var end: Long
    var correct = 0
    var msg = 0
    while (loop < max) {
        start = System.nanoTime()
        for (i in 1..rep) {
            socket.send(packet)
            socket.receive(inPacket)
            if (Data.checkArray(inPacket.data, ack))
                correct++
        }
        if (correct == rep)
            msg++
        end = System.nanoTime() - start
        loop++
        Data.timeConvert(end)
        correct = 0
    }
    println("$msg responses correct")
    socket.close()

}

//private fun udp(option: Int, max: Int){
//    val socket = DatagramSocket()
//    val ip = InetAddress.getByName(Data.address)
//
//    var rep = 0
//    var byteArray = ByteArray(1)
//    when(option){
//        1 -> { byteArray = Data.padArray(4 * 1024); rep = 256 }
//        2 -> { byteArray = Data.padArray(2 * 1024); rep = 523 }
//        3 -> { byteArray = Data.padArray(1024); rep = 1024}
//    }
//
//    var loop = 0
//    val packet = DatagramPacket(byteArray, byteArray.size, ip, Data.port)
//    val ack = ByteArray(1)
//    ack[0] = 0x1
//    val inPacket = DatagramPacket(ack, ack.size, ip, Data.port)
//
//    var start: Long
//    var end: Long
//    var correct = 0
//    while(loop < max){
//        start = System.nanoTime()
//        for(i in 1..rep){
//            socket.send(packet)
////            socket.receive(inPacket)
////            if(Data.checkArray(inPacket.data, ack))
////                msg++
//            TimeUnit.MILLISECONDS.sleep(15)
//        }
//        socket.receive(inPacket)
//        end = System.nanoTime() - start
//        if(Data.checkArray(inPacket.data, ack)) {
//            correct++
//            Data.timeConvert(end)
//            loop++
//        }
//    }
//    println("$correct responses correct")
//    socket.close()
//
//}
