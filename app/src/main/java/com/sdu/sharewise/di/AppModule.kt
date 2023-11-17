package com.sdu.sharewise.di

import com.google.firebase.auth.FirebaseAuth
import com.sdu.sharewise.data.AuthRepository
import com.sdu.sharewise.data.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAUthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}