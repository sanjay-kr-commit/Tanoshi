package logic.helper

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import logic.downloader.Downloader.download
import tanoshi.lib.util.validateDir
import tanoshi.source.api.model.component.Common
import tanoshi.source.api.model.component.Manga
import tanoshi.source.api.model.component.MangaChapter
import tanoshi.source.api.model.component.MangaPage
import java.io.File
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmClassName

fun loadImage( imagePath : String ) :Painter = try {
    val file = File(imagePath)
    val img: ImageBitmap = loadImageBitmap(file.inputStream())
    BitmapPainter(img)
} catch ( e : Exception ) {
    loadImageBitmap( JvmClassName::class.java.classLoader.getResourceAsStream( "not_found.jpg"  )!! ).run {
        BitmapPainter( this )
    }
}


fun Any.resolveCoverImage( cache : Boolean = true ) : String {

    val path = if ( cache ) this.resolveCachePath() else this.resolveDataPath()
    val coverPath = "$path/CoverImage"
    if ( coverPath.doesFileExist() ) return coverPath
    download(
        url = this.resolveImageSource()!! ,
        path ,
        "CoverImage"
    )
    return  coverPath
}

private fun Any.resolveDataPath() : String {
    val path = StringBuilder(  "${Preferences.getDataLocation}/" )
    try {
        val source = this as Common<*>
        path.append( source.extensionName )
        path.append( "/" )
        path.append( source.title!! )
    } catch ( _ : Exception ) {
        throw Exception( "Invalid Object" )
    }
    if ( !path.toString().doesDirectoryExist() ) path.toString().toFile().mkdirs()
    return path.toString()
}

private fun Any.resolveCachePath() : String {
    val path = StringBuilder(  "${Preferences.getCacheLocation}/" )
    try {
        val source = this as Common<*>
        path.append( source.extensionName )
        path.append( "/" )
        path.append( source.title!! )
    } catch ( _ : Exception ) {
        throw Exception( "Invalid Object" )
    }
    if ( !path.toString().doesDirectoryExist() ) path.toString().toFile().mkdirs()
    return path.toString()
}

private fun Any.resolveImageSource() : String? = try {
    (this as Common<*>).thumbnail_url
} catch (_:Exception){
    null
}

fun String.doesFileExist() = File( this ).isFile

fun String.doesDirectoryExist() = File( this ).isDirectory

fun String.toFile() : File = File( this )


fun Manga.resolveMangaPage( chapter : List<MangaPage> , pageIndex : Int , cache : Boolean = true ) : String {

    val path = "${if ( cache ) this.resolveCachePath() else this.resolveDataPath()}/${chapter[pageIndex-1].index}".validateDir()
    val imagePath = "$path/$pageIndex"
    if ( imagePath.doesFileExist() ) return imagePath
    download(
        url = chapter[pageIndex-1].url ,
        path ,
        pageIndex.toString()
    )
    return imagePath
}