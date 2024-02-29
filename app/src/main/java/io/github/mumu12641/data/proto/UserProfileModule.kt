package io.github.mumu12641.data.proto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mumu12641.UserProfile
import javax.inject.Singleton


val Context.profileDataStore: DataStore<UserProfile> by dataStore(
    fileName = "UserProfile.pb",
    serializer = UserProfileSerializer
)

@Module
@InstallIn(SingletonComponent::class)
class UserProfileModule {

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<UserProfile> {
        return context.profileDataStore
    }

    @Singleton
    @Provides
    fun provideUserRepository(dataStore: DataStore<UserProfile>): UserProfileRepository {
        return UserProfileRepository(dataStore)
    }

}