//package com.sako.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.sako.ui.screen.auth.ConfirmPasswordScreen
//import com.sako.ui.screen.auth.ForgetPasswordScreen
//import com.sako.ui.screen.auth.LoginScreen
//import com.sako.ui.screen.auth.RegisterScreen
//import com.sako.ui.screen.home.HomeScreen
//import com.sako.ui.screen.kuis.KuisDetailScreen
//import com.sako.ui.screen.kuis.KuisListScreen
//import com.sako.ui.screen.kuis.QuizAttempScreen
//import com.sako.ui.screen.kuis.QuizResultScreen
//import com.sako.ui.screen.map.DetailMapScreen
//import com.sako.ui.screen.map.EditUlasanScreen
//import com.sako.ui.screen.map.MapScreen
//import com.sako.ui.screen.map.ScanMapScreen
//import com.sako.ui.screen.map.TambahUlasanScreen
//import com.sako.ui.screen.profile.BadgeDetailScreen
//import com.sako.ui.screen.profile.BadgeListScreen
//import com.sako.ui.screen.profile.ChangePasswordScreen
//import com.sako.ui.screen.profile.EditProfileScreen
//import com.sako.ui.screen.profile.ProfileScreen
//import com.sako.ui.screen.profile.SetttingScreen
//import com.sako.ui.screen.video.VideoDetailScreen
//import com.sako.ui.screen.video.VideoFavoriteScreen
//import com.sako.ui.screen.video.VideoListScreen
//import com.sako.ui.screen.welcome.SplashScreen
//
///**
// * SAKO Navigation Graph - Mendefinisikan semua navigasi dan routing aplikasi
// *
// * @param navController NavHostController untuk handle navigasi
// * @param modifier Modifier untuk styling tambahan
// * @param startDestination Route awal aplikasi (default: splash)
// */
//@Composable
//fun SakoNavGraph(
//    navController: NavHostController,
//    modifier: Modifier = Modifier,
//    startDestination: String = Screen.Splash.route
//) {
//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//        modifier = modifier
//    ) {
//
////        // ============================================
////        // Welcome & Authentication Flow
////        // ============================================
////
////        composable(route = Screen.Splash.route) {
////            SplashScreen(
////                onNavigateToLogin = {
////                    navController.navigate(Screen.Login.route) {
////                        popUpTo(Screen.Splash.route) { inclusive = true }
////                    }
////                },
////                onNavigateToHome = {
////                    navController.navigate(Screen.Home.route) {
////                        popUpTo(Screen.Splash.route) { inclusive = true }
////                    }
////                }
////            )
////        }
////
////        composable(route = Screen.Login.route) {
////            LoginScreen(
////                onNavigateToRegister = {
////                    navController.navigate(Screen.Register.route)
////                },
////                onNavigateToForgetPassword = {
////                    navController.navigate(Screen.ForgetPassword.route)
////                },
////                onLoginSuccess = {
////                    navController.navigate(Screen.Home.route) {
////                        popUpTo(Screen.Login.route) { inclusive = true }
////                    }
////                }
////            )
////        }
////
////        composable(route = Screen.Register.route) {
////            RegisterScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onRegisterSuccess = {
////                    navController.popBackStack()
////                }
////            )
////        }
////
////        composable(route = Screen.ForgetPassword.route) {
////            ForgetPasswordScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onNavigateToConfirmPassword = {
////                    navController.navigate(Screen.ConfirmPassword.route)
////                }
////            )
////        }
////
////        composable(route = Screen.ConfirmPassword.route) {
////            ConfirmPasswordScreen(
////                onNavigateToLogin = {
////                    navController.navigate(Screen.Login.route) {
////                        popUpTo(Screen.ForgetPassword.route) { inclusive = true }
////                    }
////                }
////            )
////        }
//
//        // ============================================
//        // Main Bottom Navigation Screens
//        // ============================================
//
//        composable(route = Screen.Home.route) {
//            HomeScreen(navController = navController)
//        }
//
//        composable(route = Screen.VideoList.route) {
//            VideoListScreen(
//                onNavigateToDetail = { videoId ->
//                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
//                },
//                onNavigateToFavorite = {
//                    navController.navigate(Screen.VideoFavorite.route)
//                }
//            )
//        }
//
//        composable(route = Screen.KuisList.route) {
//            KuisListScreen(
//                onNavigateToDetail = { categoryId ->
//                    navController.navigate(Screen.KuisDetail.createRoute(categoryId))
//                }
//            )
//        }
//
//        composable(route = Screen.Map.route) {
//            MapScreen(
//                onNavigateToDetail = { locationId ->
//                    navController.navigate(Screen.MapDetail.createRoute(locationId))
//                },
//                onNavigateToScan = {
//                    navController.navigate(Screen.ScanMap.route)
//                }
//            )
//        }
//
//        composable(route = Screen.Profile.route) {
//            ProfileScreen(
//                onNavigateToSetting = {
//                    navController.navigate(Screen.Setting.route)
//                },
//                onNavigateToBadgeList = {
//                    navController.navigate(Screen.BadgeList.route)
//                },
//                onNavigateToBadgeDetail = { badgeId ->
//                    navController.navigate(Screen.BadgeDetail.createRoute(badgeId))
//                }
//            )
//        }
//
////        // ============================================
////        // Kuis Module
////        // ============================================
////
////        composable(
////            route = Screen.KuisDetail.route,
////            arguments = listOf(
////                navArgument(NavArgs.CATEGORY_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val categoryId = backStackEntry.arguments?.getString(NavArgs.CATEGORY_ID) ?: ""
////            KuisDetailScreen(
////                categoryId = categoryId,
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onNavigateToQuiz = { levelId ->
////                    navController.navigate(Screen.QuizAttempt.createRoute(levelId))
////                }
////            )
////        }
////
////        composable(
////            route = Screen.QuizAttempt.route,
////            arguments = listOf(
////                navArgument(NavArgs.LEVEL_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val levelId = backStackEntry.arguments?.getString(NavArgs.LEVEL_ID) ?: ""
////            QuizAttempScreen(
////                levelId = levelId,
////                onNavigateToResult = { attemptId ->
////                    navController.navigate(Screen.QuizResult.createRoute(attemptId)) {
////                        popUpTo(Screen.KuisDetail.route)
////                    }
////                },
////                onNavigateBack = {
////                    navController.popBackStack()
////                }
////            )
////        }
////
////        composable(
////            route = Screen.QuizResult.route,
////            arguments = listOf(
////                navArgument(NavArgs.ATTEMPT_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val attemptId = backStackEntry.arguments?.getString(NavArgs.ATTEMPT_ID) ?: ""
////            QuizResultScreen(
////                attemptId = attemptId,
////                onNavigateToHome = {
////                    navController.navigate(Screen.Home.route) {
////                        popUpTo(Screen.KuisList.route)
////                    }
////                },
////                onNavigateToKuisList = {
////                    navController.navigate(Screen.KuisList.route) {
////                        popUpTo(Screen.KuisList.route) { inclusive = true }
////                    }
////                }
////            )
////        }
//
////        // ============================================
////        // Video Module
////        // ============================================
////
////        composable(
////            route = Screen.VideoDetail.route,
////            arguments = listOf(
////                navArgument(NavArgs.VIDEO_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val videoId = backStackEntry.arguments?.getString(NavArgs.VIDEO_ID) ?: ""
////            VideoDetailScreen(
////                videoId = videoId,
////                onNavigateBack = {
////                    navController.popBackStack()
////                }
////            )
////        }
////
////        composable(route = Screen.VideoFavorite.route) {
////            VideoFavoriteScreen(
////                onNavigateToDetail = { videoId ->
////                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
////                },
////                onNavigateBack = {
////                    navController.popBackStack()
////                }
////            )
////        }
//
////        // ============================================
////        // Map Module
////        // ============================================
////
////        composable(
////            route = Screen.MapDetail.route,
////            arguments = listOf(
////                navArgument(NavArgs.LOCATION_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val locationId = backStackEntry.arguments?.getString(NavArgs.LOCATION_ID) ?: ""
////            DetailMapScreen(
////                locationId = locationId,
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onNavigateToScan = {
////                    navController.navigate(Screen.ScanMap.route)
////                },
////                onNavigateToTambahUlasan = {
////                    navController.navigate(Screen.TambahUlasan.createRoute(locationId))
////                },
////                onNavigateToEditUlasan = { reviewId ->
////                    navController.navigate(Screen.EditUlasan.createRoute(reviewId))
////                }
////            )
////        }
////
////        composable(route = Screen.ScanMap.route) {
////            ScanMapScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onScanSuccess = { locationId ->
////                    navController.navigate(Screen.MapDetail.createRoute(locationId)) {
////                        popUpTo(Screen.Map.route)
////                    }
////                }
////            )
////        }
////
////        composable(
////            route = Screen.TambahUlasan.route,
////            arguments = listOf(
////                navArgument(NavArgs.LOCATION_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val locationId = backStackEntry.arguments?.getString(NavArgs.LOCATION_ID) ?: ""
////            TambahUlasanScreen(
////                locationId = locationId,
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onSubmitSuccess = {
////                    navController.popBackStack()
////                }
////            )
////        }
////
////        composable(
////            route = Screen.EditUlasan.route,
////            arguments = listOf(
////                navArgument(NavArgs.REVIEW_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val reviewId = backStackEntry.arguments?.getString(NavArgs.REVIEW_ID) ?: ""
////            EditUlasanScreen(
////                reviewId = reviewId,
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onUpdateSuccess = {
////                    navController.popBackStack()
////                },
////                onDeleteSuccess = {
////                    navController.popBackStack()
////                }
////            )
////        }
//
////        // ============================================
////        // Profile Module
////        // ============================================
////
////        composable(route = Screen.Setting.route) {
////            SettitngScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onNavigateToEditProfile = {
////                    navController.navigate(Screen.EditProfile.route)
////                },
////                onNavigateToChangePassword = {
////                    navController.navigate(Screen.ChangePassword.route)
////                },
////                onLogout = {
////                    navController.navigate(Screen.Login.route) {
////                        popUpTo(0) { inclusive = true }
////                    }
////                }
////            )
////        }
////
////        composable(route = Screen.EditProfile.route) {
////            EditProfileScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onUpdateSuccess = {
////                    navController.popBackStack()
////                }
////            )
////        }
////
////        composable(route = Screen.ChangePassword.route) {
////            ChangePasswordScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onChangeSuccess = {
////                    navController.popBackStack()
////                }
////            )
////        }
////
////        composable(route = Screen.BadgeList.route) {
////            BadgeListScreen(
////                onNavigateBack = {
////                    navController.popBackStack()
////                },
////                onNavigateToDetail = { badgeId ->
////                    navController.navigate(Screen.BadgeDetail.createRoute(badgeId))
////                }
////            )
////        }
////
////        composable(
////            route = Screen.BadgeDetail.route,
////            arguments = listOf(
////                navArgument(NavArgs.BADGE_ID) { type = NavType.StringType }
////            )
////        ) { backStackEntry ->
////            val badgeId = backStackEntry.arguments?.getString(NavArgs.BADGE_ID) ?: ""
////            BadgeDetailScreen(
////                badgeId = badgeId,
////                onNavigateBack = {
////                    navController.popBackStack()
////                }
////            )
////        }
//    }
//}