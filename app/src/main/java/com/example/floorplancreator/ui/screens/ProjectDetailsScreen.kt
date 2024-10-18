package com.example.floorplancreator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.floorplancreator.data.Room
import com.example.floorplancreator.ui.UiState

@Composable
fun ProjectDetailsScreen(
    uiState: UiState,
    onAddRoom: (String, Float, Float, Float) -> Unit,
    onRoomSelected: (String) -> Unit,
    onDeleteProject: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var roomName by remember { mutableStateOf("") }
    var roomWidth by remember { mutableStateOf("") }
    var roomLength by remember { mutableStateOf("") }
    var roomHeight by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("Add New Room")
        }

        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.ProjectDetails -> {
                Text(
                    text = "Project: ${uiState.projectWithRooms.project.name}",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(16.dp)
                )
                RoomList(uiState.projectWithRooms.rooms, onRoomSelected)
            }
            else -> {} // Handle other states if needed
        }

        Button(
            onClick = onDeleteProject,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("Delete Project")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Room") },
            text = {
                Column {
                    TextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        label = { Text("Room Name") }
                    )
                    TextField(
                        value = roomWidth,
                        onValueChange = { roomWidth = it },
                        label = { Text("Width (m)") }
                    )
                    TextField(
                        value = roomLength,
                        onValueChange = { roomLength = it },
                        label = { Text("Length (m)") }
                    )
                    TextField(
                        value = roomHeight,
                        onValueChange = { roomHeight = it },
                        label = { Text("Height (m)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (roomName.isNotBlank() && roomWidth.isNotBlank() && roomLength.isNotBlank() && roomHeight.isNotBlank()) {
                            onAddRoom(
                                roomName,
                                roomWidth.toFloatOrNull() ?: 0f,
                                roomLength.toFloatOrNull() ?: 0f,
                                roomHeight.toFloatOrNull() ?: 0f
                            )
                            showDialog = false
                            roomName = ""
                            roomWidth = ""
                            roomLength = ""
                            roomHeight = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RoomList(rooms: List<Room>, onRoomSelected: (String) -> Unit) {
    LazyColumn {
        items(rooms) { room ->
            RoomItem(room, onRoomSelected)
        }
    }
}

@Composable
fun RoomItem(room: Room, onRoomSelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onRoomSelected(room.id) },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = room.name, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dimensions: ${room.width}m x ${room.length}m x ${room.height}m",
                style = MaterialTheme.typography.body2
            )
        }
    }
}