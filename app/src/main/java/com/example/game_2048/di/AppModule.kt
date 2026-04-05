package com.example.game_2048.di

import com.example.game_2048.config.FeatureFlags
import com.example.game_2048.domain.engine.GameEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGameEngine(): GameEngine = GameEngine()

    @Provides
    @Singleton
    fun provideFeatureFlags(): FeatureFlags = FeatureFlags()
}
