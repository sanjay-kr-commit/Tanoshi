package ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExtendedSearchBar(
    text : MutableState<String>,
    style: SearchBarStyle = SearchBarStyle(),
    width: MutableState<Int>,
    extendRequest : MutableState<Boolean>
) {
    Box( Modifier
        .padding( 10.dp )
        .height( 55.dp )
        .width( width.value.dp )
        .clip( CircleShape )
        .background( style.backgroundColor )
    ) {
        Row( Modifier.fillMaxSize() , verticalAlignment = Alignment.CenterVertically ) {
            Icon(
                style.searchIcon ,
                 "" ,
                Modifier
                    .background( style.backgroundColor )
                    .padding( 10.dp )
                    .onClick {
                             extendRequest.value = false
                    }
                ,
                tint = Color.Red
            )
            if ( extendRequest.value ) TextField(
                text.value ,
                onValueChange = {
                    text.value = it
                } ,
                Modifier
                    .fillMaxSize() ,
                singleLine = true ,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = style.backgroundColor ,
                    textColor = style.textColor ,
                    cursorColor = style.cursorColor ,
                    focusedIndicatorColor = style.focusedIndicatorColor ,
                    unfocusedIndicatorColor = style.unfocusedIndicatorColor ,
                    focusedLabelColor = style.focusedLabelColor ,
                    unfocusedLabelColor = style.unfocusedLabelColor
                ) ,
                label = {
                    Text(
                        style.labelText ,
                        fontWeight = style.fontWeight
                    )
                } ,
                textStyle = TextStyle(
                    fontWeight = style.fontWeight
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContractedSearchBar(
    style: SearchBarStyle = SearchBarStyle() ,
    width : MutableState<Int> = mutableStateOf( 55 ) ,
    extendRequest : MutableState<Boolean>
) {
    Box( Modifier
        .padding( 10.dp )
        .height( 55.dp )
        .width( width.value.dp )
        .clip( CircleShape )
        .fillMaxSize()
        .background( style.backgroundColor )
    ) {
        Icon(
            style.searchIcon ,
            "" ,
            Modifier
                .background( style.backgroundColor )
                .padding( 10.dp )
                .onClick {
                         extendRequest.value = true
                }
            ,
            tint = Color.Red
        )
    }
}

@Composable
fun SearchBar(
    text : MutableState<String> ,
    style: SearchBarStyle = SearchBarStyle() ,
    extendedStatus: MutableState<Boolean>? = null
) {
    val isExtended = extendedStatus ?:remember { mutableStateOf( false ) }
    val extendRequest = remember { mutableStateOf( false ) }
    val size = remember { mutableStateOf(IntSize.Zero) }
    Row(
        Modifier
            .fillMaxSize()
            .onSizeChanged {
                size.value = it
            }
    ){
      if ( isExtended.value ) {
          val width = remember { mutableStateOf( size.value.width ) }
          ExtendedSearchBar( text , style , width , extendRequest )
          if ( !extendRequest.value ) {
              CoroutineScope( Dispatchers.Unconfined ).launch {
                  for ( i in size.value.width downTo 55 step 2 ) {
                      delay( 1 )
                      width.value = i
                  }
                  isExtended.value = false
              }
          }

      } else {
          val width = mutableStateOf( 55 )
          ContractedSearchBar( style , width , extendRequest )
          if ( extendRequest.value ) {
              CoroutineScope( Dispatchers.Unconfined ).launch {
                  for ( i in 55 .. size.value.width step 2 ) {
                      delay( 1 )
                      width.value = i
                  }
                  isExtended.value = true
              }
          }
      }
    }
}

data class SearchBarStyle(
    val backgroundColor: Color = Color.Black ,
    val textColor : Color = Color.White ,
    val searchIconColor : Color = Color.Red ,
    val cursorColor : Color = Color.White ,
    val focusedIndicatorColor : Color = Color.White ,
    val unfocusedIndicatorColor : Color = Color.Red ,
    val focusedLabelColor : Color = Color.White ,
    val unfocusedLabelColor : Color = Color.Red ,
    val fontWeight: FontWeight = FontWeight.Bold ,
    val searchIcon : ImageVector = Icons.Filled.Search ,
    val labelText : String = "Search"
)