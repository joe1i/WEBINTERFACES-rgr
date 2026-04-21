package com.example.announcementchannel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.announcementchannel.ui.LoginScreen
import com.example.announcementchannel.ui.RegisterScreen
import com.example.announcementchannel.ui.theme.AnnouncementChannelTheme
import com.example.announcementchannel.viewmodel.AuthViewModel
import com.example.announcementchannel.viewmodel.AnnouncementsViewModel
import com.example.announcementchannel.ui.AnnouncementsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnnouncementChannelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("announcements") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                // Звідси переходимо на реєстрацію
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onRegisterSuccess = {
                                    navController.navigate("announcements") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("announcements") {
                            val announcementsViewModel: AnnouncementsViewModel = viewModel()
                            AnnouncementsScreen(viewModel = announcementsViewModel)
                        }
                    }
                }
            }
        }
    }
}