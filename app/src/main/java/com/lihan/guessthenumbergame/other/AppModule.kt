package com.lihan.guessthenumbergame.other

import android.content.Context
import com.lihan.guessthenumbergame.repositories.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun providerHomeRepository(@ApplicationContext context : Context) = HomeRepository(context)


}