package com.example.floorplancreator.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rooms")
data class Room(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val width: Float,
    val length: Float,
    val height: Float
)

@Entity(tableName = "walls")
data class Wall(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val roomId: String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)

data class ProjectWithRooms(
    val project: Project,
    val rooms: List<Room>
)

data class RoomWithWalls(
    val room: Room,
    val walls: List<Wall>
)