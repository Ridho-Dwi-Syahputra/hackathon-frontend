package com.sako.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sako.ui.screen.auth.LoginScreen
import com.sako.ui.screen.auth.RegisterScreen
import com.sako.ui.screen.home.HomeScreen
import com.sako.ui.screen.kuis.QuizCategoryChooseScreen
import com.sako.ui.screen.kuis.QuizLevelChooseScreen
import com.sako.ui.screen.kuis.QuizAttemptScreen
import com.sako.ui.screen.kuis.QuizResultScreen
import com.sako.ui.screen.welcome.SplashScreen
import com.sako.viewmodel.AuthViewModel
import com.sako.viewmodel.KuisViewModel
import com.sako.viewmodel.QuizAttemptViewModel
import com.sako.viewmodel.VideoViewModel
import com.sako.viewmodel.ViewModelFactory
import com.sako.ui.screen.video.VideoListScreen
import com.sako.ui.screen.video.VideoDetailScreen
import com.sako.ui.screen.video.VideoFavoriteScreen
import androidx.compose.runtime.collectAsState
import com.sako.data.pref.UserPreference

@Composable
fun SakoNavGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    userPreference: UserPreference,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route // Start dari Splash Screen
) {
    // Create a single shared VideoViewModel for all video-related screens so favorites
    // are consistent across navigation destinations.
    val sharedVideoViewModel: VideoViewModel = viewModel(factory = viewModelFactory)
    
    // Create AuthViewModel for login and register screens
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // ============================================
        // Welcome & Authentication Screens
        // ============================================
        
        // Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                userPreference = userPreference
            )
        }

        // Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel,
                userPreference = userPreference
            )
        }

        // ============================================
        // Home Screen
        // ============================================
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToQuiz = {
                    navController.navigate(Screen.KuisList.route)
                },
                onNavigateToVideo = {
                    navController.navigate(Screen.VideoList.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                }
            )
        }

        // ============================================
        // Quiz Module - Category List
        // ============================================
        composable(route = Screen.KuisList.route) {
            val kuisViewModel: KuisViewModel = viewModel(factory = viewModelFactory)

            QuizCategoryChooseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLevelList = { categoryId ->
                    navController.navigate(Screen.KuisDetail.createRoute(categoryId))
                },
                viewModel = kuisViewModel
            )
        }

        // ============================================
        // Quiz Module - Level List per Category
        // ============================================
        composable(
            route = Screen.KuisDetail.route,
            arguments = listOf(
                navArgument(NavArgs.CATEGORY_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(NavArgs.CATEGORY_ID) ?: ""
            val kuisViewModel: KuisViewModel = viewModel(factory = viewModelFactory)

            QuizLevelChooseScreen(
                categoryId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQuiz = { levelId ->
                    navController.navigate(Screen.QuizAttempt.createRoute(levelId))
                },
                viewModel = kuisViewModel
            )
        }

        // ============================================
        // Quiz Module - Active Quiz Attempt
        // ============================================
        composable(
            route = Screen.QuizAttempt.route,
            arguments = listOf(
                navArgument(NavArgs.LEVEL_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getString(NavArgs.LEVEL_ID) ?: ""
            val quizAttemptViewModel: QuizAttemptViewModel = viewModel(factory = viewModelFactory)

            QuizAttemptScreen(
                levelId = levelId,
                onNavigateToResult = { attemptId ->
                    navController.navigate(Screen.QuizResult.createRoute(attemptId)) {
                        popUpTo(Screen.KuisDetail.route)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = quizAttemptViewModel
            )
        }

        // ============================================
        // Quiz Module - Quiz Result
        // ============================================
        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(
                navArgument(NavArgs.ATTEMPT_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val attemptId = backStackEntry.arguments?.getString(NavArgs.ATTEMPT_ID) ?: ""
            val quizAttemptViewModel: QuizAttemptViewModel = viewModel(factory = viewModelFactory)

            QuizResultScreen(
                attemptId = attemptId,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.KuisList.route) { inclusive = true }
                    }
                },
                onNavigateToCategoryList = {
                    navController.navigate(Screen.KuisList.route) {
                        popUpTo(Screen.KuisList.route) { inclusive = true }
                    }
                },
                viewModel = quizAttemptViewModel
            )
        }

        // ============================================
        // Map Module Screens
        // ============================================
        composable(route = Screen.Map.route) {
            val mapViewModel: com.sako.viewmodel.MapViewModel = viewModel(factory = viewModelFactory)

            com.sako.ui.screen.map.MapScreen(
                viewModel = mapViewModel,
                onNavigateToDetail = { placeId ->
                    navController.navigate(Screen.MapDetail.createRoute(placeId))
                },
                onNavigateToScan = {
                    navController.navigate(Screen.ScanMap.route)
                }
            )
        }

        composable(
            route = Screen.MapDetail.route,
            arguments = listOf(
                navArgument(NavArgs.LOCATION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString(NavArgs.LOCATION_ID) ?: ""
            val mapViewModel: com.sako.viewmodel.MapViewModel = viewModel(factory = viewModelFactory)
            val profileViewModel: com.sako.viewmodel.ProfileViewModel = viewModel(factory = viewModelFactory)
            val userProfile = profileViewModel.userProfile.collectAsState().value

            com.sako.ui.screen.map.DetailMapScreen(
                placeId = placeId,
                viewModel = mapViewModel,
                currentUserId = (userProfile as? com.sako.utils.Resource.Success)?.data?.user?.id,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddReview = { locationId ->
                    // Get place name from detail
                    val placeDetail = mapViewModel.touristPlaceDetail.value
                    val placeName = (placeDetail as? com.sako.utils.Resource.Success)?.data?.place?.name ?: "Tempat Wisata"
                    navController.navigate(Screen.TambahUlasan.createRoute(locationId, placeName))
                },
                onNavigateToEditReview = { reviewId, locationId, rating, reviewText ->
                    navController.navigate(Screen.EditUlasan.createRoute(reviewId, locationId, rating, reviewText))
                }
            )
        }

        composable(route = Screen.ScanMap.route) {
            val mapViewModel: com.sako.viewmodel.MapViewModel = viewModel(factory = viewModelFactory)

            com.sako.ui.screen.map.ScanMapScreen(
                viewModel = mapViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { placeId ->
                    navController.navigate(Screen.MapDetail.createRoute(placeId)) {
                        popUpTo(Screen.Map.route)
                    }
                }
            )
        }

        composable(
            route = Screen.TambahUlasan.route,
            arguments = listOf(
                navArgument(NavArgs.LOCATION_ID) { type = NavType.StringType },
                navArgument(NavArgs.PLACE_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString(NavArgs.LOCATION_ID) ?: ""
            val placeName = backStackEntry.arguments?.getString(NavArgs.PLACE_NAME) ?: ""
            val mapViewModel: com.sako.viewmodel.MapViewModel = viewModel(factory = viewModelFactory)

            com.sako.ui.screen.map.TambahUlasanScreen(
                placeId = placeId,
                placeName = placeName,
                viewModel = mapViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditUlasan.route,
            arguments = listOf(
                navArgument(NavArgs.REVIEW_ID) { type = NavType.StringType },
                navArgument(NavArgs.PLACE_ID) { type = NavType.StringType },
                navArgument(NavArgs.RATING) { type = NavType.IntType },
                navArgument(NavArgs.REVIEW_TEXT) { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString(NavArgs.REVIEW_ID) ?: ""
            val placeId = backStackEntry.arguments?.getString(NavArgs.PLACE_ID) ?: ""
            val rating = backStackEntry.arguments?.getInt(NavArgs.RATING) ?: 0
            val reviewText = backStackEntry.arguments?.getString(NavArgs.REVIEW_TEXT)
                ?.let { if (it == "null") null else it }
            val mapViewModel: com.sako.viewmodel.MapViewModel = viewModel(factory = viewModelFactory)

            com.sako.ui.screen.map.EditUlasanScreen(
                reviewId = reviewId,
                placeId = placeId,
                initialRating = rating,
                initialReviewText = reviewText,
                viewModel = mapViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ============================================
        // Video Module Screens
        // ============================================
        composable(route = Screen.VideoList.route) {
            // Pass the shared ViewModel's list into the list screen so it shows
            // the same data (and favorites) as other screens.
            VideoListScreen(
                videos = sharedVideoViewModel.videos.collectAsState().value,
                onNavigateToFavorite = {
                    navController.navigate(Screen.VideoFavorite.route)
                },
                onNavigateToDetail = { videoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                }
            )
        }

        composable(route = Screen.VideoFavorite.route) {
            VideoFavoriteScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { videoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                },
                viewModel = sharedVideoViewModel
            )
        }

        composable(
            route = Screen.VideoDetail.route,
            arguments = listOf(navArgument(NavArgs.VIDEO_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString(NavArgs.VIDEO_ID) ?: ""

            // Set selected video when entering detail screen using the shared VM
            sharedVideoViewModel.setSelectedVideo(videoId)

            VideoDetailScreen(
                videoId = videoId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onToggleFavorite = { id ->
                    sharedVideoViewModel.toggleVideoFavorite(id)
                },
                onNavigateToVideo = { newVideoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(newVideoId))
                },
                video = sharedVideoViewModel.selectedVideo.collectAsState().value,
                videos = sharedVideoViewModel.videos.collectAsState()
            )
        }
    }
}
