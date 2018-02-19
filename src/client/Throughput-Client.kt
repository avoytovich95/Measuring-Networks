package client

import java.io.*
import java.net.*
import java.util.*

/**
 * Created by Alex on 2/8/2018.
 */
fun main(args: Array<String>){
    val scan = Scanner(System.`in`)
    print("Select package size (KB): ")
        val length = scan.nextInt()
    print("Enter package count: ")
        val max = scan.nextInt()

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
        val bytes = Data.padArray(length * 1024)

        var loops = 0
        var start: Long
        var end: Long
        var byteIn: Any?
        var count = 0
        while(loops < max) {
            start = System.nanoTime()
            output?.writeObject(bytes)
            byteIn = input?.readObject()
            end = System.nanoTime() - start

            Data.timeConvert(end / 2)
            if(Data.checkArray(bytes, byteIn as ByteArray))
                count++
            loops++
        }
        println("$count messages correct")

        input?.close()
        output?.close()
        socket?.close()

    } catch (e: IOException) {
        println("IO Failure")
        e.printStackTrace()
    }
}