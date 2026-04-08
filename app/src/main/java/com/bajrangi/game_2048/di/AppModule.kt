package com.bajrangi.game_2048.di

import android.content.Context
import androidx.room.Room
import com.bajrangi.game_2048.data.local.GameDatabase
import com.bajrangi.game_2048.data.local.ScoreDao
import com.bajrangi.game_2048.data.repository.GameRepositoryImpl
import com.bajrangi.game_2048.domain.repository.GameRepository
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
    fun provideDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "numra_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideScoreDao(database: GameDatabase): ScoreDao {
        return database.scoreDao()
    }

    @Provides
    @Singleton
    fun provideGameRepository(scoreDao: ScoreDao): GameRepository {
        return GameRepositoryImpl(scoreDao)
    }
}
