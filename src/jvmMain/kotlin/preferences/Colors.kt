package preferences

import androidx.compose.ui.graphics.Color

enum class Colors( val value : Color ) {
    StartScreenBackground( Color.Black ) ,
    WidgetColor( Color.White ) ,
    SelectedIcon( Color.Black ) ,
    UnselectedIcon( Color.DarkGray ) ,
    Text( Color.Black ) ,
    Progress( Color.Red )
}