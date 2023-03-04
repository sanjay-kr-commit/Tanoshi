package logic.downloader

import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Paths

class Download( url: String , filePath : String ) {

    var fileSize : Long = -1
    var progress : Long = 0
    var status : Boolean = false

    var url : URL

    init {
        this.url = URL( url )
        getFileSize( this.url )
        downloadFile( this.url , filePath )
    }

    private fun getFileSize(url : URL ) {
        var conn : HttpURLConnection? = null
        try {
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "HEAD"
            fileSize = conn.getHeaderField( "content-length" ).toLong()
        } catch ( _ : Exception ) {
        } finally {
            conn?.let { it.disconnect() }
        }
    }

    private fun downloadFile(url: URL, fileName: String) {

        try {
            url.openStream().use { inputStream ->

                BufferedInputStream(inputStream).use { bis ->

                    FileOutputStream("$fileName.unfinishedDownload").use { fos ->

                        val data = ByteArray(1024)

                        var count: Int

                        while (bis.read(data, 0, 1024).also { count = it } != -1) {

                            fos.write(data, 0, count)
                            progress += count
                            print("\r$progress/$fileSize")

                        }

                    }

                }

            }

            Paths.get("$fileName.unfinishedDownload").toFile().renameTo(Paths.get(fileName).toFile())

            print("\rDownload Complete\n")
        } catch (
            e: Exception
        ) {
            println( e )
        }
        status = true

    }

}