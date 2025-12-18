package com.sako.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.sako.ui.screen.profile.ProfileScreen
import com.sako.ui.screen.profile.SettingScreen
import com.sako.ui.screen.profile.EditProfileScreen
import com.sako.ui.screen.profile.ChangePasswordScreen
import com.sako.ui.screen.profile.AboutSystemScreen
import com.sako.viewmodel.AuthViewModel
import com.sako.viewmodel.KuisViewModel
import com.sako.viewmodel.QuizAttemptViewModel
import com.sako.viewmodel.VideoViewModel
import com.sako.viewmodel.VideoCollectionViewModel
import com.sako.viewmodel.ProfileViewModel
import com.sako.viewmodel.ViewModelFactory
import com.sako.ui.screen.video.VideoListScreen
import com.sako.ui.screen.video.VideoDetailScreen
import com.sako.ui.screen.video.VideoFavoriteScreen
import com.sako.ui.screen.video.VideoCollectionListScreen
import com.sako.ui.screen.video.VideoCollectionDetailScreen
import com.sako.ui.screen.video.CreateCollectionDialog
import com.sako.ui.screen.video.AddToCollectionBottomSheet
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.sako.data.pref.UserPreference
import com.sako.di.Injection

@Composable
fun SakoNavGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    userPreference: UserPreference,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route // Start dari Splash Screen
) {
    // Create single shared ViewModels at NavGraph level to prevent recreation on navigation
    // This ensures data persistence and prevents redundant API calls
    // Using unified ViewModelFactory for all modules
    
    // Shared ViewModels for all modules using single factory
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val sharedProfileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
    val sharedVideoViewModel: VideoViewModel = viewModel(factory = viewModelFactory)
    val sharedKuisViewModel: KuisViewModel = viewModel(factory = viewModelFactory)
    val sharedVideoCollectionViewModel: VideoCollectionViewModel = viewModel(factory = viewModelFactory)
    val sharedMapViewModel: com.sako.viewmodel.MapViewModel = viewModel(factory = viewModelFactory)

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
            QuizCategoryChooseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLevelList = { categoryId ->
                    navController.navigate(Screen.KuisDetail.createRoute(categoryId))
                },
                viewModel = sharedKuisViewModel
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

            QuizLevelChooseScreen(
                categoryId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQuiz = { levelId ->
                    navController.navigate(Screen.QuizAttempt.createRoute(levelId))
                },
                viewModel = sharedKuisViewModel
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
            // Get parent entry to share ViewModel across quiz screens
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.KuisList.route)
            }
            val quizAttemptViewModel: QuizAttemptViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = viewModelFactory
            )

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
            // Get parent entry to share ViewModel with QuizAttemptScreen
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.KuisList.route)
            }
            val quizAttemptViewModel: QuizAttemptViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = viewModelFactory
            )

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
            com.sako.ui.screen.map.MapScreen(
                viewModel = sharedMapViewModel,
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

            com.sako.ui.screen.map.DetailMapScreen(
                placeId = placeId,
                viewModel = sharedMapViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToScan = {
                    navController.navigate(Screen.ScanMap.route)
                },
                onNavigateToAddReview = { locationId ->
                    // Get place name from detail
                    val placeDetail = sharedMapViewModel.touristPlaceDetail.value
                    val placeName = (placeDetail as? com.sako.utils.Resource.Success)?.data?.name ?: "Tempat Wisata"
                    navController.navigate(Screen.TambahUlasan.createRoute(locationId, placeName))
                },
                onNavigateToEditReview = { reviewId, locationId, placeName, rating, reviewText ->
                    navController.navigate(Screen.EditUlasan.createRoute(reviewId, locationId, placeName, rating, reviewText))
                }
            )
        }

        composable(route = Screen.ScanMap.route) {
            com.sako.ui.screen.map.ScanMapScreen(
                viewModel = sharedMapViewModel,
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

            com.sako.ui.screen.map.TambahUlasanScreen(
                placeId = placeId,
                placeName = placeName,
                viewModel = sharedMapViewModel,
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
                navArgument(NavArgs.PLACE_NAME) { type = NavType.StringType },
                navArgument(NavArgs.RATING) { type = NavType.IntType },
                navArgument(NavArgs.REVIEW_TEXT) { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString(NavArgs.REVIEW_ID) ?: ""
            val placeId = backStackEntry.arguments?.getString(NavArgs.PLACE_ID) ?: ""
            val placeName = backStackEntry.arguments?.getString(NavArgs.PLACE_NAME) ?: ""
            val rating = backStackEntry.arguments?.getInt(NavArgs.RATING) ?: 0
            val reviewText = backStackEntry.arguments?.getString(NavArgs.REVIEW_TEXT)
                ?.let { if (it == "null") null else it }

            com.sako.ui.screen.map.EditUlasanScreen(
                reviewId = reviewId,
                placeId = placeId,
                placeName = placeName,
                initialRating = rating,
                initialReviewText = reviewText,
                viewModel = sharedMapViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ============================================
        // Video Module Screens
        // ============================================
        
        composable(route = Screen.VideoList.route) {
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val sharedVideoViewModel: VideoViewModel = viewModel(
                factory = viewModelFactory,
                viewModelStoreOwner = backStackEntry
            )
            
            VideoListScreen(
                videos = sharedVideoViewModel.videos.collectAsState().value,
                onNavigateToFavorite = {
                    // Reload favorites sebelum navigate
                    sharedVideoViewModel.loadFavoriteVideos()
                    navController.navigate(Screen.VideoFavorite.route)
                },
                onNavigateToDetail = { videoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                }
            )
        }

        composable(route = Screen.VideoFavorite.route) {
            // Use shared ViewModel from Home route
            val backStackEntry = remember(it) {
                try {
                    navController.getBackStackEntry(Screen.Home.route)
                } catch (e: Exception) {
                    null
                }
            }
            
            val sharedVideoViewModel: VideoViewModel = if (backStackEntry != null) {
                viewModel(
                    factory = viewModelFactory,
                    viewModelStoreOwner = backStackEntry
                )
            } else {
                viewModel(factory = viewModelFactory)
            }
            
            val collectionViewModel: VideoCollectionViewModel = if (backStackEntry != null) {
                viewModel(
                    factory = viewModelFactory,
                    viewModelStoreOwner = backStackEntry
                )
            } else {
                viewModel(factory = viewModelFactory)
            }
            
            // Reload favorites saat masuk screen
            sharedVideoViewModel.loadFavoriteVideos()
            
            VideoFavoriteScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { videoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                },
                onNavigateToCollections = {
                    navController.navigate(Screen.VideoCollectionList.route)
                },
                viewModel = sharedVideoViewModel,
                collectionViewModel = collectionViewModel
            )
        }

        composable(route = Screen.VideoCollectionList.route) {
            val collections by sharedVideoCollectionViewModel.collections.collectAsState()
            val isLoading by sharedVideoCollectionViewModel.isLoading.collectAsState()
            var showCreateDialog by remember { mutableStateOf(false) }

            // Reload collections when this screen appears
            LaunchedEffect(Unit) {
                sharedVideoCollectionViewModel.loadCollections()
            }

            VideoCollectionListScreen(
                collections = collections,
                isLoading = isLoading,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDetail = { collectionId ->
                    navController.navigate(Screen.VideoCollectionDetail.createRoute(collectionId))
                },
                onCreateCollection = {
                    showCreateDialog = true
                }
            )

            if (showCreateDialog) {
                CreateCollectionDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreate = { nama, deskripsi ->
                        sharedVideoCollectionViewModel.createCollection(nama, deskripsi)
                        showCreateDialog = false
                    },
                    isLoading = isLoading
                )
            }
        }

        composable(
            route = Screen.VideoCollectionDetail.route,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: ""
            val selectedCollection by sharedVideoCollectionViewModel.selectedCollection.collectAsState()
            val collectionVideos by sharedVideoCollectionViewModel.collectionVideos.collectAsState()
            val isLoading by sharedVideoCollectionViewModel.isLoading.collectAsState()

            // Load collection detail when collectionId changes
            LaunchedEffect(collectionId) {
                sharedVideoCollectionViewModel.loadCollectionDetail(collectionId)
            }

            VideoCollectionDetailScreen(
                collectionName = selectedCollection?.namaKoleksi ?: "Loading...",
                collectionDescription = selectedCollection?.deskripsi,
                videoCount = selectedCollection?.jumlahVideo ?: 0,
                videos = collectionVideos,
                isLoading = isLoading,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVideoDetail = { videoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                },
                onRemoveVideo = { videoId ->
                    sharedVideoCollectionViewModel.removeVideoFromCollection(collectionId, videoId)
                },
                onDeleteCollection = {
                    sharedVideoCollectionViewModel.deleteCollection(collectionId)
                    // Wait a bit before navigating back to let the state update
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.VideoDetail.route,
            arguments = listOf(navArgument(NavArgs.VIDEO_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString(NavArgs.VIDEO_ID) ?: ""

            // Set selected video when entering detail screen
            LaunchedEffect(videoId) {
                sharedVideoViewModel.setSelectedVideo(videoId)
            }

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

        // ============================================
        // Profile Module Screens
        // ============================================
        
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                viewModel = sharedProfileViewModel
            )
        }

        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                viewModel = sharedProfileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(
                viewModel = sharedProfileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AboutSystem.route) {
            AboutSystemScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.BadgeList.route) {
            // TODO: Implement BadgeListScreen
            // Placeholder for now
        }

        composable(route = Screen.Setting.route) {
            SettingScreen(
                viewModel = sharedProfileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.AboutSystem.route)
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
