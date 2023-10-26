package com.babyfilx.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.babyflix.mobileapp.R

val fontsFamily = FontFamily(
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semi_bold, FontWeight.SemiBold),
)

val fontsFamilyPoppins = FontFamily(
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_extra_bold, FontWeight.ExtraBold),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
)


// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)