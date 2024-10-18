package com.example.floorplancreator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.floorplancreator.data.Wall
import com.example.floorplancreator.ui.UiState
import kotlin.math.roundToInt

@Composable
fun RoomDetailsScreen(
    uiState: UiState,
    onAddWall: (Float, Float, Float, Float) -> Unit,
    onUpdateWall: (Wall) -> Unit,
    onDeleteWall: (Wall) -> Unit,
    onDeleteRoom: (String) -> Unit
) {
    var drawingWall by remember { mutableStateOf(false) }
    var startPoint by remember { mutableStateOf(Offset.Zero) }
    var endPoint by remember { mutableStateOf(Offset.Zero) }
    var selectedWall by remember { mutableStateOf<Wall?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.RoomDetails -> {
                Text(
                    text = "Room: ${uiState.roomWithWalls.room.name}",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(16.dp)
                )
                RoomCanvas(
                    walls = uiState.roomWithWalls.walls,
                    onWallAdded = { startX, startY, endX, endY ->
                        onAddWall(startX, startY, endX, endY)
                    },
                    onWallSelected = { wall ->
                        selectedWall = wall
                    },
                    onWallMoved = { wall, newStartX, newStartY, newEndX, newEndY ->
                        onUpdateWall(wall.copy(startX = newStartX, startY = newStartY, endX = newEndX, endY = newEndY))
                    }
                )
                if (selectedWall != null) {
                    Button(
                        onClick = {
                            onDeleteWall(selectedWall!!)
                            selectedWall = null
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    ) {
                        Text("Delete Selected Wall")
                    }
                }
                Button(
                    onClick = { onDeleteRoom(uiState.roomWithWalls.room.projectId) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Text("Delete Room")
                }
            }
            else -> {} // Handle other states if needed
        }
    }
}

@Composable
fun RoomCanvas(
    walls: List<Wall>,
    onWallAdded: (Float, Float, Float, Float) -> Unit,
    onWallSelected: (Wall) -> Unit,
    onWallMoved: (Wall, Float, Float, Float, Float) -> Unit
) {
    var drawingWall by remember { mutableStateOf(false)}
    var startPoint by remember { mutableStateOf(Offset.Zero) }
    var endPoint by remember { mutableStateOf(Offset.Zero) }
    var selectedWall by remember { mutableStateOf<Wall?>(null) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        drawingWall = true
                        startPoint = offset
                        endPoint = offset
                    },
                    onDrag = { change, _ ->
                        endPoint = change.position
                    },
                    onDragEnd = {
                        drawingWall = false
                        onWallAdded(
                            startPoint.x,
                            startPoint.y,
                            endPoint.x,
                            endPoint.y
                        )
                    }
                )
            }
    ) {
        walls.forEach { wall ->
            drawLine(
                color = if (wall == selectedWall) Color.Red else Color.Black,
                start = Offset(wall.startX, wall.startY),
                end = Offset(wall.endX, wall.endY),
                strokeWidth = 5f
            )
        }

        if (drawingWall) {
            drawLine(
                color = Color.Gray,
                start = startPoint,
                end = endPoint,
                strokeWidth = 5f
            )
        }
    }
}

<boltAction type="file" filePath="app/src/main/java/com/example/floorplancreator/util/ErrorHandler.kt">
package com.example.floorplancreator.util

import javax.inject.Inject

class ErrorHandler @Inject constructor() {
    suspend fun <T> runCatching(block: suspend () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}