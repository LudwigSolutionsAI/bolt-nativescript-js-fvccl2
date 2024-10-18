package com.example.floorplancreator.di

import android.content.Context
import androidx.room.Room
import com.example.floorplancreator.data.AppDatabase
import com.example.floorplancreator.data.ProjectDao
import com.example.floorplancreator.data.ProjectRepository
import com.example.floorplancreator.util.ErrorHandler
import com.example.floorplancreator.util.RoomScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "floor_plan_creator_db"
        ).build()
    }

    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideProjectRepository(projectDao: ProjectDao): ProjectRepository {
        return ProjectRepository(projectDao)
    }

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    @Singleton
    fun provideRoomScanner(@ApplicationContext context: Context): RoomScanner {
        return RoomScanner(context)
    }
}