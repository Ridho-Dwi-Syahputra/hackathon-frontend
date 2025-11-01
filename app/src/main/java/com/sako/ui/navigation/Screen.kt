package com.sako.ui.navigation

/**
 * Screen Routes - Sealed class untuk mendefinisikan semua route navigasi
 * Sesuai dengan dokumen KAK Alur PTB
 */
sealed class Screen(val route: String) {

    // Welcome & Auth Screens
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgetPassword : Screen("forget_password")
    object ConfirmPassword : Screen("confirm_password")

    // Main Bottom Nav Screens
    object Home : Screen("home")
    object VideoList : Screen("video_list")
    object KuisList : Screen("kuis_list")
    object Map : Screen("map")
    object Profile : Screen("profile")

    // Kuis Module Screens
    object KuisDetail : Screen("kuis_detail/{categoryId}") {
        fun createRoute(categoryId: String) = "kuis_detail/$categoryId"
    }
    object QuizAttempt : Screen("quiz_attempt/{levelId}") {
        fun createRoute(levelId: String) = "quiz_attempt/$levelId"
    }
    object QuizResult : Screen("quiz_result/{attemptId}") {
        fun createRoute(attemptId: String) = "quiz_result/$attemptId"
    }

    // Video Module Screens
    object VideoDetail : Screen("video_detail/{videoId}") {
        fun createRoute(videoId: String) = "video_detail/$videoId"
    }
    object VideoFavorite : Screen("video_favorite")

    // Map Module Screens
    object MapDetail : Screen("map_detail/{locationId}") {
        fun createRoute(locationId: String) = "map_detail/$locationId"
    }
    object ScanMap : Screen("scan_map")
    object TambahUlasan : Screen("tambah_ulasan/{locationId}") {
        fun createRoute(locationId: String) = "tambah_ulasan/$locationId"
    }
    object EditUlasan : Screen("edit_ulasan/{reviewId}") {
        fun createRoute(reviewId: String) = "edit_ulasan/$reviewId"
    }

    // Profile Module Screens
    object Setting : Screen("setting")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")
    object BadgeList : Screen("badge_list")
    object BadgeDetail : Screen("badge_detail/{badgeId}") {
        fun createRoute(badgeId: String) = "badge_detail/$badgeId"
    }
}

/**
 * Navigation Arguments - Konstanta untuk argument keys
 */
object NavArgs {
    const val CATEGORY_ID = "categoryId"
    const val LEVEL_ID = "levelId"
    const val ATTEMPT_ID = "attemptId"
    const val VIDEO_ID = "videoId"
    const val LOCATION_ID = "locationId"
    const val REVIEW_ID = "reviewId"
    const val BADGE_ID = "badgeId"
}

/**
 * Bottom Navigation Routes - Routes yang menampilkan bottom nav
 */
val bottomNavRoutes = listOf(
    Screen.Home.route,
    Screen.VideoList.route,
    Screen.KuisList.route,
    Screen.Map.route,
    Screen.Profile.route
)