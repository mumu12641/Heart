package io.github.mumu12641.data.proto

import androidx.datastore.core.DataStore
import io.github.mumu12641.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val userProfilesStore: DataStore<UserProfile>,
) {
    val userProfileFlow: Flow<UserProfile> = userProfilesStore.data

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfilesStore.updateData {
            userProfile
        }
    }

}