package logic.downloader

import logic.helper.toFile
import java.io.FileOutputStream
import java.net.URI
import java.nio.channels.Channels

object Downloader {

    fun download( url : String , path : String , name : String? = null ) : Boolean = try {
        val fileName = name?.let {
            name
        } ?: url.toString().removePrefix("https://").replace( "/" , "-" )
        val unfinishedDonwload = "$path/$fileName.temp".toFile()
        val finishedDonwload = "$path/$fileName".toFile()
        URI(url).toURL().openStream().use { inputStream ->
            Channels.newChannel( inputStream ).use { byteStream ->
                FileOutputStream( unfinishedDonwload ).use { fileOutputStream ->
                    fileOutputStream.channel.transferFrom( byteStream , 0 , Long.MAX_VALUE )
                }
            }
        }
        unfinishedDonwload.renameTo( finishedDonwload )
        true
    } catch ( e : Exception ) {
        false
    }

}