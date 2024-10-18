package com.example.floorplancreator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.floorplancreator.ui.MainViewModel
import com.example.floorplancreator.ui.UiState

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Floor Plan Creator") }) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "projects",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("projects") {
                ProjectListScreen(
                    uiState = uiState,
                    onCreateProject = viewModel::createNewProject,
                    onProjectSelected = { projectId ->
                        navController.navigate("project/$projectId")
                    }
                )
            }
            composable(
                "project/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                LaunchedEffect(projectId) {
                    viewModel.loadProjectDetails(projectId)
                }
                ProjectDetailsScreen(
                    uiState = uiState,
                    onAddRoom = { name, width, length, height ->
                        viewModel.addRoom(projectId, name, width, length, height)
                    },
                    onRoomSelected = { roomId ->
                        navController.navigate("room/$roomId")
                    },
                    onDeleteProject = {
                        viewModel.deleteProject(projectId)
                        navController.popBackStack()
                    }
                )
            }
            composable(
                "room/{roomId}",
                arguments = listOf(navArgument("roomId") { type = NavType.StringType })
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
                LaunchedEffect(roomId) {
                    viewModel.loadRoomDetails(roomId)
                }
                RoomDetailsScreen(
                    uiState = uiState,
                    onAddWall = { startX, startY, endX, endY ->
                        viewModel.addWall(roomId, startX, startY, endX, endY)
                    },
                    onUpdateWall = viewModel::updateWall,
                    onDeleteWall = viewModel::deleteWall,
                    onDeleteRoom = { projectId ->
                        viewModel.deleteRoom(roomId, projectId)
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    if (uiState is UiState.Error) {
        ErrorDialog(
            errorMessage = (uiState as UiState.Error).message,
            onDismiss = { viewModel.loadProjects() }
        )
    }
}

@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(errorMessage) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}