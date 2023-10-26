package com.babyfilx

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.babyflix.mobileapp.R

enum class BottomBarScreen(
    val id: Int,
    val title: String,
    val routes: String,
    val icon: Int
){
    Dashboard(1, "Dashboard", "dashboard", R.drawable.icondashboard),
    Home(
    2,
    "Gallery", "home", R.drawable.icongallery
    ),
    Flix10KSubscription(3,"Flix10K", "flix10k", R.drawable.iconflix10k),
    FlixCam(4, "FlixCam", "flixcam", R.drawable.iconflixcam),
    More(5,"More", "more", R.drawable.more__unselected),
    ImageSelection(6, "image selection", "image selection", R.drawable.iconflix10k),
    ExperienceFlix10K(6, "Experience Flix10K", "experience flix10K", R.drawable.iconflix10k),
    News(5, "News", "news", R.drawable.more__unselected),
    NewsDetails(5, "News Details", "news details", R.drawable.more__unselected),
    EnhancementComplete(6, "enhancement complete", "enhancement_complete", R.drawable.iconflix10k)
}


