package ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.IconButton(
    icon : ImageVector ,
    tint : Color ,
    backgroundColor : Color ,
    height : Int = 55 ,
    onClick : () -> Unit = {}
){
    Box( Modifier.padding( 10.dp )
        .clip( CircleShape )
        .background( backgroundColor )
        .height( height.dp )
        .wrapContentWidth()
        .onClick {
            onClick()
        }
        .weight( 1f ) ,
        contentAlignment = Alignment.Center ) {
        Icon(
            icon , "" ,
            tint = tint
        )
    }
}