package com.example.floorplancreator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floorplancreator.data.*
import com.example.floorplancreator.util.ErrorHandler
import com.example.floorplancreator.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            repository.allProjects.collect { projects ->
                _uiState.value = UiState.ProjectList(projects)
            }
        }
    }

    fun createNewProject(name: String) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                val project = Project(name = name)
                repository.insertProject(project)
                project
            }
            when (result) {
                is Result.Success -> loadProjects()
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    fun loadProjectDetails(projectId: String) {
        viewModelScope.launch {
            repository.getProjectWithRooms(projectId).collect { projectWithRooms ->
                _uiState.value = UiState.ProjectDetails(projectWithRooms)
            }
        }
    }

    fun addRoom(projectId: String, name: String, width: Float, length: Float, height: Float) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                val room = Room(projectId = projectId, name = name, width = width, length = length, height = height)
                repository.insertRoom(room)
                room
            }
            when (result) {
                is Result.Success -> loadProjectDetails(projectId)
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    fun loadRoomDetails(roomId: String) {
        viewModelScope.launch {
            repository.getRoomWithWalls(roomId).collect { roomWithWalls ->
                _uiState.value = UiState.RoomDetails(roomWithWalls)
            }
        }
    }

    fun addWall(roomId: String, startX: Float, startY: Float, endX: Float, endY: Float) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                val wall = Wall(roomId = roomId, startX = startX, startY = startY, endX = endX, endY = endY)
                repository.insertWall(wall)
                wall
            }
            when (result) {
                is Result.Success -> loadRoomDetails(roomId)
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    fun updateWall(wall: Wall) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                repository.updateWall(wall)
            }
            when (result) {
                is Result.Success -> loadRoomDetails(wall.roomId)
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    fun deleteWall(wall: Wall) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                repository.deleteWall(wall)
            }
            when (result) {
                is Result.Success -> loadRoomDetails(wall.roomId)
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                repository.deleteProject(projectId)
            }
            when (result) {
                is Result.Success -> loadProjects()
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    fun deleteRoom(roomId: String, projectId: String) {
        viewModelScope.launch {
            val result = errorHandler.runCatching {
                repository.deleteRoom(roomId)
            }
            when (result) {
                is Result.Success -> loadProjectDetails(projectId)
                is Result.Error -> _uiState.value = UiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    data class ProjectList(val projects: List<Project>) : UiState()
    data class ProjectDetails(val projectWithRooms: ProjectWithRooms) : UiState()
    data class RoomDetails(val roomWithWalls: RoomWithWalls) : UiState()
    data class Error(val message: String) : UiState()
}