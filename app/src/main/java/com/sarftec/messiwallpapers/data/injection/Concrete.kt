package com.sarftec.messiwallpapers.data.injection

import android.content.Context
import com.sarftec.messiwallpapers.data.room.MessiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Concrete {

    @Singleton
    @Provides
    fun appDatabase(@ApplicationContext context: Context) : MessiDatabase {
        return MessiDatabase.getInstance(context)
    }
}