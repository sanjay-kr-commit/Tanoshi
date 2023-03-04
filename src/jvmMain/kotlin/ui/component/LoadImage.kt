package ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import logic.helper.loadImage

@Composable
fun LoadImage(
    imagePath : String,
    contentDescription: String? = "",
    modifier: Modifier = Modifier.width(200.dp).height(350.dp),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
){

    Image(
        loadImage( imagePath  ),
        contentDescription,
        modifier,
        alignment,
        contentScale,
        alpha,
        colorFilter
    )

}