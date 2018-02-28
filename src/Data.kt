/**
 * Created by Alex on 2/1/2018.
 */
object Data {
    const val port = 2698
    //const val address = "127.0.0.1" //Local ip
    const val address = "129.3.20.2" //altair cs server ip
    //const val address = "129.3.20.26" //pi cs server

    /** checkArray
     * compares two byte arrays for equality
     * @param byte1
     * @param byte2
     */
    fun checkArray(byte1: ByteArray, byte2: ByteArray) = byte1.contentEquals(byte2)

    /** padArray
     * Receives length and creates byte of set length
     * @param length
     * @return bytes
     */
    fun padArray(length: Int): ByteArray {
        val bytes = ByteArray(length)
        var byte: Byte = 0x0
        for (i in 0 until length) {
            bytes[i] = byte
            byte++
        }
        return bytes
    }

    /** timeConvert
     * Displays nanoseconds as milliseconds and nanoseconds
     * @param time
     */
    fun timeConvert(time: Long){
        println("$time")
        //print("${time / 1000000} ms ")
        //print("${time % 1000000} ns")
    }
}