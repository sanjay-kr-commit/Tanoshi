package ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Loading(
    backgroundColor: Color = Color.Black ,
    indicatorColor: Color = Color.Red
) {
    var selectedColor by remember { mutableStateOf( indicatorColor ) }
    val listOfColor = listOf( Color.Red , Color.White , Color.Gray , Color.DarkGray , Color.Green , Color.Magenta , Color.Yellow , Color.Blue , Color.LightGray )

        Box( Modifier
            .padding( 10.dp )
            .clip( RoundedCornerShape( 10.dp ) )
            .fillMaxSize()
            .background( backgroundColor )
            .onClick {
                selectedColor = listOfColor[ Random.nextInt( listOfColor.size ) ]
            }
            ,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator( color = selectedColor , modifier = Modifier.height( 80.dp ).width( 80.dp ) )
            CircularProgressIndicator( color = selectedColor , strokeWidth = 30.dp )
        }

}