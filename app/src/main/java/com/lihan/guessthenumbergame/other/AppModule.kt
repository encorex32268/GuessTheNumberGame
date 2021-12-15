package com.lihan.guessthenumbergame.other

import android.content.Context
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.repositories.GameRepository
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
    fun providerGameRepository(@ApplicationContext context : Context) = GameRepository(context)


    @Singleton
    @Provides
    fun providerFirebaseRepository(@ApplicationContext context : Context) = FireBaseRepository(context)

}