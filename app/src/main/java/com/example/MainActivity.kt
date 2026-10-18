package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.BOTTOM_NAV_ITEMS
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RafiqViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RafiqViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            val isDark = when (appTheme) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    RafiqApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun RafiqApp(viewModel: RafiqViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelRoute = remember(currentRoute) {
        BOTTOM_NAV_ITEMS.any { it.route == currentRoute }
    }

    val screenTitle = remember(currentRoute) {
        when {
            currentRoute == Screen.Home.route -> "رفيق المسلم"
            currentRoute == Screen.Quran.route -> "القرآن الكريم"
            currentRoute?.startsWith("quran_reader") == true -> ""
            currentRoute == Screen.Adhkar.route -> "الأذكار والأدعية"
            currentRoute?.startsWith("adhkar_category") == true -> ""
            currentRoute == Screen.Tasbeeh.route -> "المسبحة الإلكترونية"
            currentRoute == Screen.More.route -> "المزيد"
            currentRoute == Screen.Calendar.route -> "التقويم الهجري"
            currentRoute == Screen.DailyWird.route -> "وردي اليومي"
            currentRoute == Screen.Tadabbur.route -> "دفتر التدبر"
            currentRoute == Screen.Search.route -> "البحث الشامل"
            currentRoute == Screen.Settings.route -> "الإعدادات"
            else -> "رفيق المسلم"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Hide top bar on screens that have their own custom top app bar
            if (currentRoute?.startsWith("quran_reader") != true &&
                currentRoute?.startsWith("adhkar_category") != true &&
                currentRoute != Screen.Search.route &&
                currentRoute != Screen.Settings.route
            ) {
                AppTopBar(
                    title = screenTitle,
                    canNavigateBack = !isTopLevelRoute,
                    onNavigateBack = { navController.popBackStack() },
                    onSearchClick = if (currentRoute != Screen.Search.route) {
                        { navController.navigate(Screen.Search.route) }
                    } else null
                )
            }
        },
        bottomBar = {
            if (isTopLevelRoute) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    BOTTOM_NAV_ITEMS.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Home
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToQuran = { s, a ->
                        navController.navigate(Screen.QuranReader.createRoute(s ?: 1, a ?: 1))
                    },
                    onNavigateToAdhkarCategory = { cat ->
                        navController.navigate(Screen.AdhkarCategory.createRoute(cat))
                    },
                    onNavigateToTasbeeh = {
                        navController.navigate(Screen.Tasbeeh.route)
                    },
                    onNavigateToWird = {
                        navController.navigate(Screen.DailyWird.route)
                    },
                    onNavigateToCalendar = {
                        navController.navigate(Screen.Calendar.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToTadabbur = {
                        navController.navigate(Screen.Tadabbur.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // Quran
            composable(Screen.Quran.route) {
                QuranScreen(
                    viewModel = viewModel,
                    onOpenSurah = { surah, ayah ->
                        navController.navigate(Screen.QuranReader.createRoute(surah, ayah))
                    }
                )
            }

            // Quran Reader
            composable(
                route = Screen.QuranReader.route,
                arguments = listOf(
                    navArgument("surahNumber") { type = NavType.IntType; defaultValue = 1 },
                    navArgument("ayah") { type = NavType.IntType; defaultValue = 1 }
                )
            ) { backStack ->
                val sNum = backStack.arguments?.getInt("surahNumber") ?: 1
                val aNum = backStack.arguments?.getInt("ayah") ?: 1
                QuranReaderScreen(
                    surahNumber = sNum,
                    targetAyah = aNum,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTadabburWithAyah = { surah, ayah ->
                        navController.navigate("${Screen.Tadabbur.route}?surah=$surah&ayah=$ayah")
                    }
                )
            }

            // Adhkar Categories
            composable(Screen.Adhkar.route) {
                AdhkarScreen(
                    viewModel = viewModel,
                    onSelectCategory = { cat ->
                        navController.navigate(Screen.AdhkarCategory.createRoute(cat))
                    }
                )
            }

            // Adhkar Category Detail
            composable(
                route = Screen.AdhkarCategory.route,
                arguments = listOf(navArgument("categoryKey") { type = NavType.StringType })
            ) { backStack ->
                val catKey = backStack.arguments?.getString("categoryKey") ?: "morning"
                AdhkarCategoryScreen(
                    categoryKey = catKey,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tasbeeh
            composable(Screen.Tasbeeh.route) {
                TasbeehScreen(viewModel = viewModel)
            }

            // More
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToWird = { navController.navigate(Screen.DailyWird.route) },
                    onNavigateToTadabbur = { navController.navigate(Screen.Tadabbur.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Calendar
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Daily Wird
            composable(Screen.DailyWird.route) {
                DailyWirdScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tadabbur
            composable(
                route = "${Screen.Tadabbur.route}?surah={surah}&ayah={ayah}",
                arguments = listOf(
                    navArgument("surah") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("ayah") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStack ->
                val s = backStack.arguments?.getString("surah")?.toIntOrNull()
                val a = backStack.arguments?.getString("ayah")?.toIntOrNull()
                TadabburScreen(
                    initialSurah = s,
                    initialAyah = a,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Tadabbur.route) {
                TadabburScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Search
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenSurah = { s, a ->
                        navController.navigate(Screen.QuranReader.createRoute(s, a))
                    },
                    onOpenAdhkarCategory = { cat ->
                        navController.navigate(Screen.AdhkarCategory.createRoute(cat))
                    },
                    onOpenTadabbur = {
                        navController.navigate(Screen.Tadabbur.route)
                    }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
