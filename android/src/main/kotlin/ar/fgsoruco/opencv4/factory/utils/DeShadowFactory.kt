package ar.fgsoruco.opencv4.factory.utils

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.core.Size
import org.opencv.core.Core
import java.io.FileInputStream
import java.io.InputStream
import io.flutter.plugin.common.MethodChannel

class DeShadowFactory {
    companion object{

        fun process(pathType: Int,pathString: String, data: ByteArray, result: MethodChannel.Result) {
            when (pathType){
                1 -> result.success(deshadowS(pathString))
                2 -> result.success(deshadow(data))
                3 -> result.success(deshadow(data))
            }
        }

        private fun deshadowS(pathString: String): ByteArray? {
            val inputStream: InputStream = FileInputStream(pathString.replace("file://", ""))
            val data: ByteArray = inputStream.readBytes()
            return deshadow(data)
        }

        private fun deshadow(data: ByteArray): ByteArray? {
            return try {
                //convert to grey
                val gray = Mat()
                val src = Imgcodecs.imdecode(MatOfByte(*data), Imgcodecs.IMREAD_UNCHANGED)
                Imgproc.cvtColor(src, gray, 6) //BGR2GRAY

                //smooth picture
                val smooth = Mat()
                Imgproc.GaussianBlur(gray, smooth, Size(95.0, 95.0), 0.0)

                //divid gray by smooth, for letters quotient will be larger, for shadow will be smaller
                // instantiating an empty MatOfByte class
                val dst = Mat()
                Core.divide(gray, smooth, dst, 255.0)
                var byteArray = ByteArray(0)
                val matOfByte = MatOfByte()
                Imgcodecs.imencode(".jpg", dst, matOfByte)
                byteArray = matOfByte.toArray()
                byteArray
            } catch (e: java.lang.Exception) {
                println("OpenCV Error: $e")
                data
            }
        }
    }
}