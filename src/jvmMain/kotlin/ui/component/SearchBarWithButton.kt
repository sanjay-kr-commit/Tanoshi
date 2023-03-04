package ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarWithButton(
    query : MutableState<String> ,
    onSearchIconClick : () -> Unit = {} ,
    onClearButtonClick : () -> Unit = {}
){
    val isExtended = remember { mutableStateOf( false ) }
    Row ( Modifier.height( 80.dp ) ){
        Box( Modifier.weight( 10f ) ){
            SearchBar( query , extendedStatus = isExtended , searchAction = onSearchIconClick )
        }
        if ( isExtended.value ) IconButton( Icons.Filled.Search , Color.Red , Color.Black ) {
            onSearchIconClick()
        }
        if ( isExtended.value ) IconButton( Icons.Filled.Clear , Color.Red , Color.Black ) {
            query.value = ""
            onClearButtonClick()
        }
    }
}