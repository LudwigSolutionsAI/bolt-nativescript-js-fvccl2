package com.example.floorplancreator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProjectRepository @Inject constructor(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    fun getProjectWithRooms(projectId: String): Flow<ProjectWithRooms> {
        return projectDao.getProjectWithRooms(projectId)
    }

    fun getRoomWithWalls(roomId: String): Flow<RoomWithWalls> {
        return projectDao.getRoomWithWalls(roomId)
    }

    suspend fun insertProject(project: Project) = withContext(Dispatchers.IO) {
        projectDao.insertProject(project)
    }

    suspend fun insertRoom(room: Room) = withContext(Dispatchers.IO) {
        projectDao.insertRoom(room)
    }

    suspend fun insertWall(wall: Wall) = withContext(Dispatchers.IO) {
        projectDao.insertWall(wall)
    }

    suspend fun updateWall(wall: Wall) = withContext(Dispatchers.IO) {
        projectDao.updateWall(wall)
    }

    suspend fun deleteWall(wall: Wall) = withContext(Dispatchers.IO) {
        projectDao.deleteWall(wall)
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        projectDao.deleteProject(projectId)
    }

    suspend fun deleteRoom(roomId: String) = withContext(Dispatchers.IO) {
        projectDao.deleteRoom(roomId)
    }
}