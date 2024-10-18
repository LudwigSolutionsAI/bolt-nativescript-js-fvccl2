package com.example.floorplancreator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectWithRooms(projectId: String): Flow<ProjectWithRooms>

    @Transaction
    @Query("SELECT * FROM rooms WHERE id = :roomId")
    fun getRoomWithWalls(roomId: String): Flow<RoomWithWalls>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: Room)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWall(wall: Wall)

    @Update
    suspend fun updateWall(wall: Wall)

    @Delete
    suspend fun deleteWall(wall: Wall)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    @Query("DELETE FROM rooms WHERE id = :roomId")
    suspend fun deleteRoom(roomId: String)
}