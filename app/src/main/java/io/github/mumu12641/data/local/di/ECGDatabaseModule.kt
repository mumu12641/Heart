package io.github.mumu12641.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mumu12641.data.local.dao.ECGDao
import io.github.mumu12641.data.local.database.ECGDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideECGDao(ecgDatabase: ECGDatabase): ECGDao {
        return ecgDatabase.getECGDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ECGDatabase {
        return Room.databaseBuilder(
            appContext,
            ECGDatabase::class.java,
            "ECGModel"
        ).build()
    }
}