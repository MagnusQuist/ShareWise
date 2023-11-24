package com.sdu.sharewise.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.AuthRepositoryImpl
import com.sdu.sharewise.data.repository.UserRepository
import com.sdu.sharewise.data.repository.UserRepositoryImpl
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
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun provideFirebaseDB(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl
}