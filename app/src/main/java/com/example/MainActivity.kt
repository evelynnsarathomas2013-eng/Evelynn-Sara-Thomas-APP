package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel

class MainActivity : ComponentActivity() {

    private val eduViewModel: EduViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = (currentRoute == "home"),
                                onClick = {
                                    if (currentRoute != "home") {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                },
                                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                label = { Text("Study Search", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KidsCyanPrimary,
                                    selectedTextColor = KidsCyanPrimary,
                                    indicatorColor = SoftSkyBackground
                                )
                            )

                            NavigationBarItem(
                                selected = (currentRoute == "saved"),
                                onClick = {
                                    if (currentRoute != "saved") {
                                        navController.navigate("saved")
                                    }
                                },
                                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved Notes") },
                                label = { Text("Study Notes", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KidsCyanPrimary,
                                    selectedTextColor = KidsCyanPrimary,
                                    indicatorColor = SoftSkyBackground
                                )
                            )

                            NavigationBarItem(
                                selected = (currentRoute == "parent"),
                                onClick = {
                                    if (currentRoute != "parent") {
                                        navController.navigate("parent")
                                    }
                                },
                                icon = { Icon(Icons.Default.Lock, contentDescription = "Parent Gate") },
                                label = { Text("Parent / Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KidsCyanPrimary,
                                    selectedTextColor = KidsCyanPrimary,
                                    indicatorColor = SoftSkyBackground
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = eduViewModel,
                                onNavigateToSaved = { navController.navigate("saved") },
                                onNavigateToParent = { navController.navigate("parent") }
                            )
                        }

                        composable("saved") {
                            SavedQuestionsScreen(
                                viewModel = eduViewModel,
                                onSelectQuestion = { selectedQuestion ->
                                    eduViewModel.askStudyQuestion(selectedQuestion.questionText, selectedQuestion.subject)
                                    navController.navigate("home")
                                }
                            )
                        }

                        composable("parent") {
                            ParentGateScreen(
                                viewModel = eduViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}


