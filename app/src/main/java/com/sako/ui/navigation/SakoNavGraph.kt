package com.sako.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sako.ui.screen.kuis.QuizCategoryChooseScreen
import com.sako.ui.screen.kuis.QuizLevelChooseScreen
import com.sako.ui.screen.kuis.QuizAttemptScreen
import com.sako.ui.screen.kuis.QuizResultScreen
import com.sako.viewmodel.KuisViewModel
import com.sako.viewmodel.QuizAttemptViewModel
import com.sako.viewmodel.ViewModelFactory

@Composable
fun SakoNavGraphQuiz(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.KuisList.route // Sementara untuk testing quiz
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

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
        // Placeholder for other screens (uncomment when ready)
        // ============================================
        
        // TODO: Uncomment when implementing other modules
        /*
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToQuiz = {
                    navController.navigate(Screen.KuisList.route)
                }
            )
        }
        
        composable(route = Screen.VideoList.route) {
            VideoListScreen(...)
        }
        
        composable(route = Screen.Map.route) {
            MapScreen(...)
        }
        
        composable(route = Screen.Profile.route) {
            ProfileScreen(...)
        }
        */
    }
}
