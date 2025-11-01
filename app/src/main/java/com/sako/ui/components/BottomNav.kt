package com.sako.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sako.R
import com.sako.ui.theme.SakoTheme

/**
 * Fungsi helper untuk mendapatkan daftar item navigasi
 * Sesuaikan route dengan Screen.kt dari navigation package
 *
 * @return List dari ButtomNavItem
 */
@Composable
fun getNavigationItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            label = "Home",
            icon = painterResource(id = R.drawable.home), // Sesuaikan dengan nama file drawable
            route = "home" // Sesuaikan dengan Screen.Home.route
        ),
        BottomNavItem(
            label = "Video",
            icon = painterResource(id = R.drawable.video), // Sesuaikan dengan nama file drawable
            route = "video_list" // Sesuaikan dengan Screen.VideoList.route
        ),
        BottomNavItem(
            label = "Kuis",
            icon = painterResource(id = R.drawable.sako), // Logo SAKO untuk menu kuis
            route = "kuis_list" // Sesuaikan dengan Screen.KuisList.route
        ),
        BottomNavItem(
            label = "Map",
            icon = painterResource(id = R.drawable.map), // Sesuaikan dengan nama file drawable
            route = "map" // Sesuaikan dengan Screen.Map.route
        ),
        BottomNavItem(
            label = "Profile",
            icon = painterResource(id = R.drawable.profile), // Sesuaikan dengan nama file drawable
            route = "profile" // Sesuaikan dengan Screen.Profile.route
        )
    )
}

/**
 * SAKO Bottom Navigation Bar - Komponen navigasi utama aplikasi
 *
 * @param navController NavHostController untuk handle navigasi
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun ButtomNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val items = getNavigationItems()

    // Dapatkan current route untuk highlight item yang active
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Navigasi dengan proper back stack management
                    navController.navigate(item.route) {
                        // Pop up ke start destination untuk menghindari back stack yang besar
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Hindari multiple copies dari destination yang sama
                        launchSingleTop = true
                        // Restore state ketika navigasi kembali ke destination
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtomNavPreview() {
    SakoTheme {
        val navController = rememberNavController()
        ButtomNav(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtomNavDarkPreview() {
    SakoTheme(darkTheme = true) {
        val navController = rememberNavController()
        ButtomNav(navController = navController)
    }
}