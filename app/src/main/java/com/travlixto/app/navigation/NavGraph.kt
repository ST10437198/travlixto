package com.travlixto.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.travlixto.app.data.model.Destination
import com.travlixto.app.data.repository.AuthRepository
import com.travlixto.app.ui.screens.*
import com.travlixto.app.viewmodel.AuthViewModel

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val OTP = "otp"
    const val MAIN = "main" // hosts the bottom-nav section
    const val DETAILS = "details"
    const val EDIT_PROFILE = "edit_profile"
}

private object MainTabs {
    const val HOME = "home"
    const val SCHEDULE = "schedule"
    const val SEARCH = "search"
    const val POPULAR = "popular"
    const val PROFILE = "profile"
}

@Composable
fun TravlixtoNavGraph(navController: NavHostController) {
    val authViewModel = remember { AuthViewModel() }
    val authRepository = remember { AuthRepository() }
    val startDestination = if (authRepository.currentUser != null) Routes.MAIN else Routes.SPLASH

    // Holds the destination tapped on Home/Popular/Search so Details can read it.
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } })
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinished = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } } })
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onGoToSignUp = { navController.navigate(Routes.SIGNUP) },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = { navController.navigate(Routes.OTP) },
                onGoToSignIn = { navController.popBackStack() }
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(viewModel = authViewModel, onEmailSent = { navController.popBackStack() })
        }
        composable(Routes.OTP) {
            OtpScreen(onVerified = { navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } } })
        }
        composable(Routes.MAIN) {
            MainScaffold(
                onDestinationClick = { dest -> selectedDestination = dest; navController.navigate(Routes.DETAILS) },
                onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onSignOut = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.MAIN) { inclusive = true } } }
            )
        }
        composable(Routes.DETAILS) {
            DetailsScreen(destination = selectedDestination ?: Destination(), onBookNow = { navController.popBackStack() })
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(onDone = { navController.popBackStack() })
        }
    }
}

@Composable
private fun MainScaffold(
    onDestinationClick: (Destination) -> Unit,
    onEditProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val tabNavController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(tabNavController) }
    ) { padding ->
        NavHost(
            navController = tabNavController,
            startDestination = MainTabs.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(MainTabs.HOME) { HomeScreen(onDestinationClick) }
            composable(MainTabs.SCHEDULE) { ScheduleScreen(scheduleItems = emptyList()) }
            composable(MainTabs.SEARCH) { SearchScreen(onDestinationClick) }
            composable(MainTabs.POPULAR) { PopularPlacesScreen(onDestinationClick) }
            composable(MainTabs.PROFILE) { ProfileScreen(onEditProfile, onSignOut) }
        }
    }
}

@Composable
private fun BottomBar(navController: NavController) {
    val items = listOf(
        Triple(MainTabs.HOME, "Home", Icons.Filled.Home),
        Triple(MainTabs.SCHEDULE, "Calendar", Icons.Filled.DateRange),
        Triple(MainTabs.SEARCH, "Search", Icons.Filled.Search),
        Triple(MainTabs.POPULAR, "Places", Icons.Filled.Place),
        Triple(MainTabs.PROFILE, "Profile", Icons.Filled.Person)
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}
