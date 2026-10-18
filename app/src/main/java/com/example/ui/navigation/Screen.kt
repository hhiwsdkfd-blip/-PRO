package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quran : Screen("quran")
    object QuranReader : Screen("quran_reader/{surahNumber}?ayah={ayah}") {
        fun createRoute(surahNumber: Int, ayah: Int = 1) = "quran_reader/$surahNumber?ayah=$ayah"
    }
    object Adhkar : Screen("adhkar")
    object AdhkarCategory : Screen("adhkar_category/{categoryKey}") {
        fun createRoute(categoryKey: String) = "adhkar_category/$categoryKey"
    }
    object Tasbeeh : Screen("tasbeeh")
    object More : Screen("more")
    object Calendar : Screen("calendar")
    object DailyWird : Screen("wird")
    object Tadabbur : Screen("tadabbur")
    object Search : Screen("search")
    object Settings : Screen("settings")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

val BOTTOM_NAV_ITEMS = listOf(
    BottomNavItem(Screen.Home.route, "الرئيسية", Icons.Default.Home, "nav_home"),
    BottomNavItem(Screen.Quran.route, "القرآن", Icons.Default.Book, "nav_quran"),
    BottomNavItem(Screen.Adhkar.route, "الأذكار", Icons.Default.SelfImprovement, "nav_adhkar"),
    BottomNavItem(Screen.Tasbeeh.route, "المسبحة", Icons.Default.Fingerprint, "nav_tasbeeh"),
    BottomNavItem(Screen.More.route, "المزيد", Icons.Default.Apps, "nav_more")
)
