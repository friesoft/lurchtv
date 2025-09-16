package org.friesoft.lurchtv

import android.app.Application
import org.friesoft.lurchtv.data.repositories.VideoRepository
import org.friesoft.lurchtv.data.repositories.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class LurchTVApplication : Application()

@InstallIn(SingletonComponent::class)
@Module
abstract class VideoRepositoryModule {

    @Binds
    abstract fun bindVideoRepository(
        videoRepositoryImpl: VideoRepositoryImpl
    ): VideoRepository
}
