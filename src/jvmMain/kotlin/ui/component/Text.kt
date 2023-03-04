package ui.component

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text as DefaultText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun Text(
    text : String ,
    style : TextStyling = TextStyling()
) {DefaultText(
    text,
    style.modifier,
    style.color,
    style.fontSize,
    style.fontStyle,
    style.fontWeight,
    style.fontFamily,
    style.letterSpacing,
    style.textDecoration,
    style.textAlign,
    style.lineHeight,
    style.overflow,
    style.softWrap,
    style.maxLines,
    style.onTextLayout,
    style.style?: LocalTextStyle.current
)
}

data class TextStyling(
    val modifier: Modifier = Modifier,
    val color: Color = Color.Black ,
    val fontSize: TextUnit = TextUnit.Unspecified,
    val fontStyle: FontStyle? = null,
    val fontWeight: FontWeight? = FontWeight.SemiBold,
    val fontFamily: FontFamily? = null,
    val letterSpacing: TextUnit = TextUnit.Unspecified,
    val textDecoration: TextDecoration? = null,
    val textAlign: TextAlign? = null,
    val lineHeight: TextUnit = TextUnit.Unspecified,
    val overflow: TextOverflow = TextOverflow.Clip,
    val softWrap: Boolean = true,
    val maxLines: Int = Int.MAX_VALUE,
    val onTextLayout: (TextLayoutResult) -> Unit = {},
    val style: TextStyle? = null
)
