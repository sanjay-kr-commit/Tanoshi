package videoPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import shared.AppData
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer

@Composable
fun VideoSurface(
    mrl : String,
    sharedData : AppData ,
    modifier: Modifier = Modifier.fillMaxSize().background( Color.Black )
) {

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    imageBitmap?.let {
        androidx.compose.foundation.Image(
            bitmap = it,
            contentDescription = "Video",
            modifier = modifier ,
            contentScale = sharedData.contentScale.value
        )
    }?:run {
        Box(modifier = modifier.background(Color.Black))
    }

    val mediaPlayer = remember {
        var byteArray :ByteArray? = null
        var info: ImageInfo? = null
        val embeddedMediaPlayer = EmbeddedMediaListPlayerComponent()
        val callbackVideoSurface = CallbackVideoSurface(
            object : BufferFormatCallback {
                override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
                    info = ImageInfo.makeN32(sourceWidth, sourceHeight, ColorAlphaType.OPAQUE)
                    return RV32BufferFormat(sourceWidth, sourceHeight)
                }

                override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
                    byteArray =  ByteArray(buffers[0].limit())
                }
            },
            object : RenderCallback {
                override fun display(
                    mediaPlayer: MediaPlayer,
                    nativeBuffers: Array<out ByteBuffer>,
                    bufferFormat: BufferFormat?
                ) {
                    val byteBuffer = nativeBuffers[0]

                    byteBuffer.get(byteArray)
                    byteBuffer.rewind()

                    val bmp = Bitmap()
                    bmp.allocPixels(info!!)
                    bmp.installPixels(byteArray)
                    imageBitmap = bmp.asComposeImageBitmap()
                }
            },
            true,
            VideoSurfaceAdapters.getVideoSurfaceAdapter(),
        )
        embeddedMediaPlayer.mediaPlayer().videoSurface().set(callbackVideoSurface)
        embeddedMediaPlayer
    }

    sharedData.embeddedMediaListPlayerComponent = mediaPlayer

    LaunchedEffect(key1 = mrl) {
        mediaPlayer.mediaListPlayer().list().media().add( mrl )
        mediaPlayer.mediaListPlayer().controls().play()
    }

    DisposableEffect(key1 = mrl, effect = {
        this.onDispose {
            mediaPlayer.release()
        }
    })

}