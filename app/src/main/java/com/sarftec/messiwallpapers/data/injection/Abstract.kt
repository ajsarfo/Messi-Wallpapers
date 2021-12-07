package com.sarftec.messiwallpapers.data.injection

import com.sarftec.messiwallpapers.data.cache.UriCache
import com.sarftec.messiwallpapers.data.cache.UriCacheImpl
import com.sarftec.messiwallpapers.data.repository.FavoriteRepositoryImpl
import com.sarftec.messiwallpapers.data.repository.ImageRepositoryImpl
import com.sarftec.messiwallpapers.data.repository.QuoteRepositoryImpl
import com.sarftec.messiwallpapers.data.repository.WallpaperRepositoryImpl
import com.sarftec.messiwallpapers.domain.repository.FavoriteRepository
import com.sarftec.messiwallpapers.domain.repository.ImageRepository
import com.sarftec.messiwallpapers.domain.repository.QuoteRepository
import com.sarftec.messiwallpapers.domain.repository.WallpaperRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface Abstract {

    @Singleton
    @Binds
    fun repository(repository: WallpaperRepositoryImpl) : WallpaperRepository

    @Singleton
    @Binds
    fun imageRepository(repository: ImageRepositoryImpl) : ImageRepository

    @Singleton
    @Binds
    fun quoteRepository(repository: QuoteRepositoryImpl) : QuoteRepository

    @Singleton
    @Binds
    fun favoriteRepository(repository: FavoriteRepositoryImpl) : FavoriteRepository

    @Binds
    fun uriCache(cache: UriCacheImpl) : UriCache
}