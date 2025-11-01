package com.sako.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sako.R
import com.sako.ui.theme.SakoTheme
import com.sako.ui.theme.SakoPrimary
import com.sako.ui.theme.SakoAccent

/**
 * Fungsi helper untuk mendapatkan daftar item navigasi (tanpa center item)
 * Center akan diisi dengan FAB logo SAKO
 *
 * @return List dari BottomNavItem
 */
@Composable
fun getNavigationItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            label = "Home",
            icon = painterResource(id = R.drawable.home),
            route = "home"
        ),
        BottomNavItem(
            label = "Video",
            icon = painterResource(id = R.drawable.video),
            route = "video_list"
        ),
        // Center slot untuk FAB - dikosongkan
        BottomNavItem(
            label = "",
            icon = painterResource(id = R.drawable.sako), // Placeholder
            route = "kuis_list"
        ),
        BottomNavItem(
            label = "Map",
            icon = painterResource(id = R.drawable.map),
            route = "map"
        ),
        BottomNavItem(
            label = "Profile",
            icon = painterResource(id = R.drawable.profile),
            route = "profile"
        )
    )
}

/**
 * SAKO Bottom Navigation Bar dengan FAB di tengah
 * 
 * @param navController NavHostController untuk handle navigasi
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun BottomNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val items = getNavigationItems()

    // Dapatkan current route untuk highlight item yang active
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Bottom Navigation Bar
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = SakoPrimary,
            tonalElevation = 8.dp
        ) {
            items.forEachIndexed { index, item ->
                // Skip center position (index 2) untuk FAB
                if (index == 2) {
                    // Empty space untuk FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { },
                        enabled = false,
                        colors = NavigationBarItemDefaults.colors(
                            disabledIconColor = Color.Transparent,
                            disabledTextColor = Color.Transparent
                        )
                    )
                } else {
                    val isSelected = currentRoute == item.route

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SakoPrimary,
                            selectedTextColor = SakoPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = SakoAccent
                        )
                    )
                }
            }
        }

        // FAB dengan logo SAKO di tengah (warna asli dari PNG)
        // Background kuning ketika di halaman quiz
        val isFabSelected = currentRoute == "kuis_list"
        
        FloatingActionButton(
            onClick = {
                navController.navigate("kuis_list") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(64.dp),
            shape = CircleShape,
            containerColor = if (isFabSelected) SakoAccent else Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.sako),
                contentDescription = "Quiz",
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtomNavPreview() {
    SakoTheme {
        val navController = rememberNavController()
        BottomNav(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtomNavDarkPreview() {
    SakoTheme(darkTheme = true) {
        val navController = rememberNavController()
        BottomNav(navController = navController)
    }
}