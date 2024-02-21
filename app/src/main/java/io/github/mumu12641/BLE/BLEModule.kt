package io.github.mumu12641.BLE

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object BLEModule {

    @Provides
    fun provideBLEService(): BLEService {
        return BLEService()
    }
}
