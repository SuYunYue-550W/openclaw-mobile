package com.openclaw.mobile.di

import android.content.Context
import com.openclaw.mobile.api.ModelRepository
import com.openclaw.mobile.inference.LocalInferenceEngine
import com.openclaw.mobile.security.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage {
        return SecureStorage(context)
    }

    @Provides
    @Singleton
    fun provideModelRepository(
        secureStorage: SecureStorage
    ): ModelRepository {
        return ModelRepository(secureStorage)
    }

    @Provides
    @Singleton
    fun provideLocalInferenceEngine(
        @ApplicationContext context: Context
    ): LocalInferenceEngine {
        return LocalInferenceEngine(context)
    }
}
